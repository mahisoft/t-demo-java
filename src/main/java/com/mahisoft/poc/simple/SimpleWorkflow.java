package com.mahisoft.poc.simple;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface SimpleWorkflow {

    @WorkflowMethod(name = "Simple")
    String process(String input);
}
