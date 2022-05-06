package com.mahisoft.poc;

import com.mahisoft.poc.activities.QueryActivityImpl;
import com.mahisoft.poc.activities.RecordActivityImpl;
import com.mahisoft.poc.crawler.CrawlerWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.WorkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Worker {

    public static final Logger logger = LoggerFactory.getLogger(Worker.class);
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
    public static final WorkflowClient client = WorkflowClient.newInstance(service);
    public static final WorkerFactory factory = WorkerFactory.newInstance(client);

    public static final String QUEUE_NAME = "jdemo";

    public static void main(String[] args) {
        try {
            logger.info(String.format("Worker starting: PID %d", ProcessHandle.current().pid()));
            var worker = factory.newWorker(QUEUE_NAME);
            worker.registerWorkflowImplementationTypes(CrawlerWorkflowImpl.class);
            worker.registerActivitiesImplementations(new QueryActivityImpl(), new RecordActivityImpl());
            factory.start();
            logger.info("Worker started");
        } catch (Exception ex) {
            logger.error("Worker failed", ex);
        }
    }
}
