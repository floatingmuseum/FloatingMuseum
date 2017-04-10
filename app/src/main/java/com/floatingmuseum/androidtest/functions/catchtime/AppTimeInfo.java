package com.floatingmuseum.androidtest.functions.catchtime;

/**
 * Created by Floatingmuseum on 2017/4/10.
 */

public class AppTimeInfo {

    private String name;
    private String packageName;
    private long dayStartTime;
    private long usingTime;

    public AppTimeInfo(String name, String packageName, long dayStartTime, long usingTime) {
        this.name = name;
        this.packageName = packageName;
        this.dayStartTime = dayStartTime;
        this.usingTime = usingTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getDayStartTime() {
        return dayStartTime;
    }

    public void setDayStartTime(long dayStartTime) {
        this.dayStartTime = dayStartTime;
    }

    public long getUsingTime() {
        return usingTime;
    }

    public void setUsingTime(long usingTime) {
        this.usingTime = usingTime;
    }
}
