package com.mahisoft.poc.publish.activities.signature;

import com.mahisoft.poc.publish.activities.assets.Asset;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface SignatureActivity {

    @ActivityMethod
    void sendSalesAgreement(Asset asset);

}
