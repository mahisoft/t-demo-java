package com.mahisoft.poc.simple;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface SimpleActivity {

    @ActivityMethod
    String callA(String value);

    @ActivityMethod
    String callB(String value);

    @ActivityMethod
    String callC(String a, String b);

}
