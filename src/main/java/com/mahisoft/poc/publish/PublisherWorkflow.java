package com.mahisoft.poc.publish;


import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface PublisherWorkflow {

    @WorkflowMethod(name = "Publish")
    void publish(PublicationState listing);

    @SignalMethod
    void updateMedia(MediaStatus status);

    @SignalMethod
    void updateInspection(InspectionStatus status);

    @SignalMethod
    void updateSalesAgreement(SignatureStatus status);

    @QueryMethod
    PublicationState getState();

}
