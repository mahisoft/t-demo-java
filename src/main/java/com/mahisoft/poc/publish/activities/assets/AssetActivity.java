package com.mahisoft.poc.publish.activities.assets;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface AssetActivity {

    Asset getAsset(String id);
}
