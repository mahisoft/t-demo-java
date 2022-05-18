package com.mahisoft.poc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mahisoft.poc.publish.PublicationState;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SampleUntyped {

    public static final Logger logger = LoggerFactory.getLogger(Worker.class);


    public static void main(String[] args) throws JsonProcessingException, InterruptedException {

        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);

        WorkflowOptions options = WorkflowOptions.newBuilder().setTaskQueue(Worker.QUEUE_NAME)
                //.setWorkflowId("my-id") <- if not provided, temporal sets an uuid
                .build();

        var workflow = client.newUntypedWorkflowStub("Publish", options);

        var execution = workflow.start(Map.of("assetId", "123456"));

        logger.info("Started workflow with id {}", execution.getWorkflowId());

        workflow.signal("updateMedia", "PROCESSED");
        workflow.signal("updateInspection", "DONE");
        workflow.signal("updateSalesAgreement", "SIGNED");
    }
}
