package com.mahisoft.poc.publish;

import com.mahisoft.poc.publish.activities.assets.Address;
import com.mahisoft.poc.publish.activities.assets.Asset;
import com.mahisoft.poc.publish.activities.assets.AssetActivity;
import com.mahisoft.poc.publish.activities.assets.AssetActivityImpl;
import com.mahisoft.poc.publish.activities.inspections.InspectionActivity;
import com.mahisoft.poc.publish.activities.inspections.InspectionActivityImpl;
import com.mahisoft.poc.publish.activities.media.MediaActivity;
import com.mahisoft.poc.publish.activities.media.MediaActivityImpl;
import com.mahisoft.poc.publish.activities.signature.SignatureActivity;
import com.mahisoft.poc.publish.activities.signature.SignatureActivityImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowRule;
import org.junit.Rule;
import org.junit.Test;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestPublishWorkflow {

    @Rule
    public TestWorkflowRule testWorkflowRule =
            TestWorkflowRule.newBuilder()
                    .setWorkflowTypes(PublisherWorkflowImpl.class)
                    .setDoNotStart(true)
                    .build();

    @Test
    public void testPublish() {
        AssetActivity assetActivity = mock(AssetActivityImpl.class);
        InspectionActivity inspectionActivity = mock(InspectionActivityImpl.class);
        MediaActivity mediaActivity = mock(MediaActivityImpl.class);
        SignatureActivity signatureActivity = mock(SignatureActivityImpl.class);


        when(assetActivity.getAsset(any())).thenReturn(
                Asset.builder()
                        .address(Address.builder()
                                .line1("line1").line2("line2")
                                .state("FL").zipCode("12345")
                                .city("city").build())
                        .build());


        testWorkflowRule.getWorker().registerActivitiesImplementations(
                assetActivity, inspectionActivity, mediaActivity, signatureActivity);

        WorkflowOptions options =
                WorkflowOptions.newBuilder()
                        .setTaskQueue(testWorkflowRule.getTaskQueue())
                        .setWorkflowTaskTimeout(Duration.ofSeconds(20))
                        .setWorkflowRunTimeout(Duration.ofSeconds(20))
                        .build();

        testWorkflowRule.getTestEnvironment().start();

        PublisherWorkflow workflow = testWorkflowRule.getWorkflowClient().newWorkflowStub(PublisherWorkflow.class, options);

        testWorkflowRule.getTestEnvironment().registerDelayedCallback(Duration.ofSeconds(1), () -> {
            workflow.updateMedia(MediaStatus.PROCESSED);
            workflow.updateInspection(InspectionStatus.DONE);
        });

        testWorkflowRule.getTestEnvironment().registerDelayedCallback(Duration.ofSeconds(2), () -> {
            workflow.updateSalesAgreement(SignatureStatus.SIGNED);
        });

        workflow.publish(PublicationState.builder().assetId("123").build());

        verify(assetActivity, times(1)).getAsset(any());
        verify(inspectionActivity, times(1)).requestInspection(any());
        verify(mediaActivity, times(1)).requestNewPhotos(any());
        verify(signatureActivity, times(1)).sendSalesAgreement(any());


    }
}
