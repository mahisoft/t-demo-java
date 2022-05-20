package com.mahisoft.poc;

import com.mahisoft.poc.crawler.CrawlerWorkflowImpl;
import com.mahisoft.poc.crawler.activities.QueryActivityImpl;
import com.mahisoft.poc.crawler.activities.RecordActivityImpl;
import com.mahisoft.poc.publish.PublisherWorkflowImpl;
import com.mahisoft.poc.publish.activities.assets.AssetActivityImpl;
import com.mahisoft.poc.publish.activities.inspections.InspectionActivityImpl;
import com.mahisoft.poc.publish.activities.media.MediaActivityImpl;
import com.mahisoft.poc.publish.activities.signature.SignatureActivityImpl;
import com.mahisoft.poc.simple.SimpleActivityImpl;
import com.mahisoft.poc.simple.SimpleWorkflowImpl;
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
            worker.registerWorkflowImplementationTypes(
                    CrawlerWorkflowImpl.class,
                    PublisherWorkflowImpl.class,
                    SimpleWorkflowImpl.class);
            worker.registerActivitiesImplementations(
                    new QueryActivityImpl(),
                    new RecordActivityImpl(),
                    new AssetActivityImpl(),
                    new InspectionActivityImpl(),
                    new MediaActivityImpl(),
                    new SignatureActivityImpl(),
                    new SimpleActivityImpl());
            factory.start();
            logger.info("Worker started");
        } catch (Exception ex) {
            logger.error("Worker failed", ex);
        }
    }
}
