package com.mahisoft.poc.simple;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.Map;

@WorkflowInterface
public interface SimpleWorkflow {

    @WorkflowMethod(name = "Simple")
    String process(String input);

    @QueryMethod
    Map<String, String> getState();
}
