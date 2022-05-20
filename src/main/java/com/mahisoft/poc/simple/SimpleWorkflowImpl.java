package com.mahisoft.poc.simple;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class SimpleWorkflowImpl implements SimpleWorkflow {

    private final Logger log = Workflow.getLogger(SimpleWorkflowImpl.class);

    // We might want to load this options from configuration.
    // Also, we might want to have different settings for each activity.
    private final ActivityOptions defaultOptions = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(1))
            .setHeartbeatTimeout(Duration.ofSeconds(5))
            .build();

    private final SimpleActivity simple = Workflow.newActivityStub(SimpleActivity.class, defaultOptions);

    private Map<String, String> state = new HashMap<>();

    @Override
    public String process(String input) {
        state.put("input", input);
        log.info("calling a");
        var a = simple.callA(input);
        state.put("a", a);
        log.info("calling b");
        var b = simple.callB(a);
        state.put("b", b);
        log.info("calling c");
        var c = simple.callC(a, b);
        state.put("c", c);
        log.info("done!!!");
        return c;
    }

    @Override
    public Map<String, String> getState() {
        return state;
    }
}
