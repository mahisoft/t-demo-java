package com.mahisoft.poc.crawler;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface CrawlerWorkflow {

    @WorkflowMethod(name = "Crawler")
    void crawl(State state);

    @SignalMethod
    void changePageSize(int size);

    @QueryMethod
    State getState();

}
