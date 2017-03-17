package com.floatingmuseum.androidtest.functions.catchtime;

import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

/**
 * Created by Floatingmuseum on 2017/3/17.
 */

@RealmClass
public class AppTimeUsingInfo implements RealmModel {

    private long dayStartTime;
    private String packageName;
    private long startTime;
    private long endTime;
    private long usingTime;
}
