package com.mahisoft.poc.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface RecordActivity {

    @ActivityMethod
    void Store(Item item);
}
