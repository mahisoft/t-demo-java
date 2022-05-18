package com.mahisoft.poc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mahisoft.poc.publish.InspectionStatus;
import com.mahisoft.poc.publish.MediaStatus;
import com.mahisoft.poc.publish.PublicationState;
import com.mahisoft.poc.publish.PublisherWorkflow;
import com.mahisoft.poc.publish.SignatureStatus;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SampleTyped {


    public static final Logger logger = LoggerFactory.getLogger(Worker.class);


    public static void main(String[] args) throws JsonProcessingException, InterruptedException {

        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);

        WorkflowOptions options = WorkflowOptions.newBuilder().setTaskQueue(Worker.QUEUE_NAME)
                //.setWorkflowId("my-id") <- if not provided, temporal sets an uuid
                .build();

        var workflow = client.newWorkflowStub(PublisherWorkflow.class, options);

        var execution = WorkflowClient.start(workflow::publish, PublicationState.builder().assetId("123456").build());

        logger.info("Started workflow with id {}", execution.getWorkflowId());

        Thread.sleep(3000);
        workflow.updateMedia(MediaStatus.PROCESSED);
        workflow.updateInspection(InspectionStatus.DONE);
        Thread.sleep(3000);
        workflow.updateSalesAgreement(SignatureStatus.SIGNED);
    }
}
