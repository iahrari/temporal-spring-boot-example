package com.github.iahrari.temporal.api.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface FindDriverActivity {
    @ActivityMethod
    String findDriver(int lat, int lng);
}
