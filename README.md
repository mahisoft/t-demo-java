# Temporal Crawler DEMO

## Requirements

* Java 17
* Temporal
* Temporal command line [TCTL](https://docs.temporal.io/docs/tctl/)
* Docker
* Docker Compose

### Prepare your Environment

The docker compose used in this DEMO can be found [here](https://github.com/temporalio/docker-compose)

Clone the repository [https://github.com/temporalio/docker-compose](https://github.com/temporalio/docker-compose) and
start the version of your choice. For the DEMO we used `docker-compose-mysql-es.yml`

After cloning the repository, locate the folder and execute

```shell
docker-compose -f docker-compose-mysql-es.yml up -d
```

To access the temporal console, using your browser navigate to [http://localhost:8088](http://localhost:8088)

### Run the app

To execute the demo, clone the repository and then locate the project folder and execute

```shell
./gradlew run
```

### Start a Crawler

To start a workflow, from the command line execute:

```shell
tctl workflow start --taskqueue jdemo --workflow_type Crawler --input "{\"page\":1,\"size\":10}"
```

You should see an output like:

```
❯ tctl workflow start --taskqueue jdemo --workflow_type Crawler --input "{\"page\":1,\"size\":10}"
Started Workflow Id: 5ae13729-f3be-44a2-bdca-a7d5e1a09d19, run Id: a392d263-2ee9-41fd-b2ec-193b31552210
```

### Signal the crawler to change the page size

Copy the WorkflowID from the previous command, in this example: `5ae13729-f3be-44a2-bdca-a7d5e1a09d19`

Execute the following command

```shell
tctl workflow signal --workflow_id 5ae13729-f3be-44a2-bdca-a7d5e1a09d19 --name changePageSize --input "5"
```

You should see an output like:

```
❯ tctl workflow signal --workflow_id 5ae13729-f3be-44a2-bdca-a7d5e1a09d19 --name changePageSize --input "5"
Signal workflow succeeded.
```

### Query the crawler state

To query the workflow state execute:

```shell
tctl workflow query --workflow_id 5ae13729-f3be-44a2-bdca-a7d5e1a09d19 --query_type "getState"
```

You should see an output like:

```
❯ tctl workflow query --workflow_id 5ae13729-f3be-44a2-bdca-a7d5e1a09d19 --query_type "getState"
Query result:
[{"page":21,"size":5,"getFailed":0,"recordFailed":0,"success":145}]
```

If `--run_id` is not provided, temporal will query the last execution.

The same Query can be executed in the Temporal UI [http://localhost:8088](http://localhost:8088)

* Locate the workflow and click on the RUN ID you want to query
* In the bar menu, click on QUERY
* Select "getState" in the dropdown

You can pick other RUN IDs to query the workflow at that point in time.

_NOTE: To execute the query, the worker must be running._
