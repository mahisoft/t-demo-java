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
import org.junit.jupiter.api.Assertions;

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



        // We start the workflow using the WorkflowClient instead of executing workflow.publish(...)
        // to avoid workflow timeout.
        // This workflow implementation stays waiting for signals and only ends its execution after
        // the exit condition is true "Workflow.await(() -> current.isDone() || current.isPublicationCanceled());"
        // In order to use the workflow.publish(...) approach, all necessary registerDelayedCallback(...) must be registered
        // previous calling the publish(...) method.
        WorkflowClient.start(workflow::publish, PublicationState.builder().assetId("123").build());

        // This first delay ensure the activities are called
        testWorkflowRule.getTestEnvironment().sleep(Duration.ofSeconds(1));

        var state = workflow.getState();
        Assertions.assertEquals(MediaStatus.REQUESTED, state.getMediaStatus());
        Assertions.assertFalse(state.isDone());

        workflow.updateMedia(MediaStatus.PROCESSED);
        state = workflow.getState();
        Assertions.assertEquals(MediaStatus.PROCESSED, state.getMediaStatus());


        workflow.updateInspection(InspectionStatus.DONE);
        state = workflow.getState();
        Assertions.assertTrue(state.isReadyForSignature());
        Assertions.assertFalse(state.isDone());

        workflow.updateSalesAgreement(SignatureStatus.SENT);
        state = workflow.getState();
        Assertions.assertEquals(SignatureStatus.SENT, state.getSignatureStatus());
        Assertions.assertFalse(state.isDone());


        workflow.updateSalesAgreement(SignatureStatus.SIGNED);
        state = workflow.getState();
        Assertions.assertTrue(state.isDone());


        verify(assetActivity, times(1)).getAsset(any());
        verify(inspectionActivity, times(1)).requestInspection(any());
        verify(mediaActivity, times(1)).requestNewPhotos(any());
        verify(signatureActivity, times(1)).sendSalesAgreement(any());

        testWorkflowRule.getTestEnvironment().shutdown();

    }
}
