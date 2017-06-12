package floatingmuseum.userhunter;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by Floatingmuseum on 2017/6/12.
 */

@RealmClass
public class AppTimeUsingInfo implements RealmModel {

    private String appName;
    private String packageName;
    //记录按天划分,以每天的起始时间来区分每一天
    private long dayStartTime;
    //每次应用使用的起始时间为唯一的
    @PrimaryKey
    private long startTime;
    private long endTime;
    private long usingTime;

    public AppTimeUsingInfo() {
    }

    public AppTimeUsingInfo(String appName, String packageName, long dayStartTime, long startTime, long endTime, long usingTime) {
        this.appName = appName;
        this.packageName = packageName;
        this.dayStartTime = dayStartTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.usingTime = usingTime;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getDayStartTime() {
        return dayStartTime;
    }

    public void setDayStartTime(long dayStartTime) {
        this.dayStartTime = dayStartTime;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getUsingTime() {
        return usingTime;
    }

    public void setUsingTime(long usingTime) {
        this.usingTime = usingTime;
    }
}
