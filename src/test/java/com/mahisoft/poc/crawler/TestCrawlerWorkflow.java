package com.mahisoft.poc.crawler;

import com.mahisoft.poc.crawler.activities.Page;
import com.mahisoft.poc.crawler.activities.QueryActivity;
import com.mahisoft.poc.crawler.activities.QueryActivityImpl;
import com.mahisoft.poc.crawler.activities.RecordActivity;
import com.mahisoft.poc.crawler.activities.RecordActivityImpl;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestCrawlerWorkflow {

    @Rule
    public TestWorkflowRule testWorkflowRule =
            TestWorkflowRule.newBuilder()
                    .setWorkflowTypes(CrawlerWorkflowImpl.class)
                    .setDoNotStart(true)
                    .build();

    @Test
    public void testCrawlerImplementation() {
        QueryActivity queryActivities = mock(QueryActivityImpl.class);
        RecordActivity recordActivity = mock(RecordActivityImpl.class);

        when(queryActivities.getNextPage(any())).thenReturn(
                new Page(false, List.of("1", "2", "3")));

        testWorkflowRule.getWorker().registerActivitiesImplementations(queryActivities, recordActivity);
        testWorkflowRule.getTestEnvironment().start();

        WorkflowOptions options =
                WorkflowOptions.newBuilder().setTaskQueue(testWorkflowRule.getTaskQueue()).build();

        CrawlerWorkflow workflow = testWorkflowRule.getWorkflowClient()
                .newWorkflowStub(CrawlerWorkflow.class, options);


        workflow.crawl(State.builder().page(1).size(20).build());

        verify(queryActivities, times(1)).getNextPage(any());
        verify(queryActivities, times(3)).getItem(any());
        verify(recordActivity, times(3)).Store(any());

    }
}
