package com.mahisoft.poc.simple;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class SimpleWorkflowImpl implements SimpleWorkflow {

    private final Logger log = Workflow.getLogger(SimpleWorkflowImpl.class);

    // We might want to load this options from configuration.
    // Also, we might want to have different settings for each activity.
    private final ActivityOptions defaultOptions = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(1))
            .setHeartbeatTimeout(Duration.ofSeconds(5))
            .build();

    private final SimpleActivity simple = Workflow.newActivityStub(SimpleActivity.class, defaultOptions);


    @Override
    public String process(String input) {
        log.info("calling a");
        var a = simple.callA(input);
        log.info("calling b");
        var b = simple.callB(a);
        log.info("calling c");
        var c = simple.callC(a, b);
        log.info("done!!!");
        return c;
    }
}
