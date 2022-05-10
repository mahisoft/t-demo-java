package com.mahisoft.poc.crawler.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface RecordActivity {

    @ActivityMethod
    void Store(Item item);
}
