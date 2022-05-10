package com.mahisoft.poc.crawler.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface QueryActivity {

    @ActivityMethod()
    Page getNextPage(Query query);

    @ActivityMethod()
    Item getItem(String id);
}
