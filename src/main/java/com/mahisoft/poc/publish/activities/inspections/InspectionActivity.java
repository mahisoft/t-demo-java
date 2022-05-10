package com.mahisoft.poc.publish.activities.inspections;

import com.mahisoft.poc.publish.activities.assets.Address;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface InspectionActivity {

    @ActivityMethod
    void requestInspection(Address address);
}
