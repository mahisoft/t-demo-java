package com.mahisoft.poc.publish;

import com.mahisoft.poc.crawler.CrawlerWorkflowImpl;
import com.mahisoft.poc.publish.activities.assets.Asset;
import com.mahisoft.poc.publish.activities.assets.AssetActivity;
import com.mahisoft.poc.publish.activities.inspections.InspectionActivity;
import com.mahisoft.poc.publish.activities.media.MediaActivity;
import com.mahisoft.poc.publish.activities.signature.SignatureActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class PublisherWorkflowImpl implements PublisherWorkflow {


    private final Logger log = Workflow.getLogger(CrawlerWorkflowImpl.class);

    private PublicationState current = null;
    private Asset asset = null;

    // We might want to load this options from configuration.
    // Also, we might want to have different settings for each activity.
    private final ActivityOptions defaultOptions = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(1))
            .setHeartbeatTimeout(Duration.ofSeconds(5))
            .build();

    private final MediaActivity media = Workflow.newActivityStub(MediaActivity.class, defaultOptions);
    private final AssetActivity assets = Workflow.newActivityStub(AssetActivity.class, defaultOptions);
    private final InspectionActivity inspection = Workflow.newActivityStub(InspectionActivity.class, defaultOptions);
    private final SignatureActivity signature = Workflow.newActivityStub(SignatureActivity.class, defaultOptions);

    // We might want to set this value from configuration, or perhaps load it from
    // an external service
    private Integer inspectionExpirationDays = 60;


    @Override
    public void publish(PublicationState state) {
        log.info("starting a publication process for asset id {}", state.getAssetId());
        this.current = state;

        // This hack ensures that if the inspectionExpirationDays value is changed, either in the code
        // or by the configuration, it won't produce a non-deterministic error when the rule is evaluated below.
        inspectionExpirationDays = Workflow.sideEffect(Integer.class, () -> inspectionExpirationDays);

        asset = assets.getAsset(state.getAssetId());

        var promises = List.of(
                Async.procedure(this::requestMediaIfNeeded),
                Async.procedure(this::requestInspectionIfNeeded));

        Promise.allOf(promises);

        Workflow.await(() -> current.isReadyForSignature() || current.isPublicationCanceled());

        if (!current.isPublicationCanceled()) {
            sendForSignature();
        }

        Workflow.await(() -> current.isDone() || current.isPublicationCanceled());
    }

    @Override
    public void updateMedia(MediaStatus status) {
        current.setMediaStatus(status);
    }

    @Override
    public void updateInspection(InspectionStatus status) {
        current.setInspectionStatus(status);
    }

    @Override
    public void updateSalesAgreement(SignatureStatus status) {
        current.setSignatureStatus(status);
    }

    @Override
    public PublicationState getState() {
        return current;
    }

    private void requestMediaIfNeeded() {
        if (current.getMediaStatus() != MediaStatus.PENDING) {
            return;
        }

        try {
            media.requestNewPhotos(asset.getAddress());
            current.setMediaStatus(MediaStatus.REQUESTED);
            log.info("media request sent for asset {}", current.getAssetId());
        } catch (ActivityFailure ex) {
            current.setMediaStatus(MediaStatus.PENDING);
        }
    }

    private void requestInspectionIfNeeded() {

        boolean isExpired = asset.getLastInspectionDate() == null ||
                DAYS.between(asset.getLastInspectionDate(), Instant.ofEpochMilli(Workflow.currentTimeMillis())) > inspectionExpirationDays;

        if (current.getInspectionStatus() != InspectionStatus.PENDING && !isExpired) {
            return;
        }

        try {
            inspection.requestInspection(asset.getAddress());
            current.setInspectionStatus(InspectionStatus.REQUESTED);
            log.info("inspection request sent for asset {}", current.getAssetId());
        } catch (ActivityFailure ex) {
            current.setInspectionStatus(InspectionStatus.PENDING);
        }
    }

    private void sendForSignature() {
        if (!current.isReadyForSignature() || current.getSignatureStatus() != SignatureStatus.PENDING) {
            return;
        }

        signature.sendSalesAgreement(asset);
        current.setSignatureStatus(SignatureStatus.SENT);
        log.info("signature request sent for asset {}", current.getAssetId());
    }
}
