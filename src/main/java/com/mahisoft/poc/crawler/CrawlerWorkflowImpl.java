package com.mahisoft.poc.crawler;

import com.mahisoft.poc.crawler.activities.Item;
import com.mahisoft.poc.crawler.activities.Query;
import com.mahisoft.poc.crawler.activities.QueryActivity;
import com.mahisoft.poc.crawler.activities.RecordActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.client.ActivityCompletionException;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.ArrayList;

public class CrawlerWorkflowImpl implements CrawlerWorkflow {

    private final Logger log = Workflow.getLogger(CrawlerWorkflowImpl.class);

    // We might want to load this options from configuration.
    // Also, we might want to have different settings for each activity.
    private final ActivityOptions defaultOptions = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(1))
            .setHeartbeatTimeout(Duration.ofSeconds(5))
            .build();

    private final QueryActivity query = Workflow.newActivityStub(QueryActivity.class, defaultOptions);
    private final RecordActivity record = Workflow.newActivityStub(RecordActivity.class, defaultOptions);

    private State current = null;

    @Override
    public void crawl(State state) {
        log.info("starting crawler");
        current = state;

        var page = query.getNextPage(new Query(current.getPage(), current.getSize()));

        var promises = new ArrayList<Promise<Void>>();
        for (String id : page.ids()) {
            promises.add(Async.procedure(this::processItem, id));
        }

        Workflow.await(() -> Promise.allOf(promises).isCompleted());

        if (!page.hasNext()) {
            return;
        }

        // We want to continue the workflow as new to avoid having a workflow history too long.
        Workflow.continueAsNew(current.nextPage());
    }

    @Override
    public void changePageSize(int size) {
        log.info("changing page size to {}", size);
        current.setSize(size);
    }

    @Override
    public State getState() {
        return current;
    }

    private void processItem(String id) {
        try {
            Item item = query.getItem(id);
            record.Store(item);
        } catch (ActivityCompletionException ex) {
            log.error(String.format("error executing %s activity for item %s", ex.getActivityType(), id));
            current.addFailed();
            throw ex;
        }
        current.addSuccess();
    }
}
