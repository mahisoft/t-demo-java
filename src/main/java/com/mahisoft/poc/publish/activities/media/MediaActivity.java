package com.mahisoft.poc.publish.activities.media;

import com.mahisoft.poc.publish.activities.assets.Address;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface MediaActivity {

    @ActivityMethod
    void requestNewPhotos(Address address);
}
