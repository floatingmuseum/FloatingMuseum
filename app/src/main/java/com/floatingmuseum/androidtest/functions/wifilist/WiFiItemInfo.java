package com.floatingmuseum.androidtest.functions.wifilist;

/**
 * Created by Floatingmuseum on 2017/5/25.
 */

public class WiFiItemInfo {

    private String name;
    private String bssid;
    private String desc;
    private int level;
    private int speed;
    private String capabilities;
    private boolean isLock;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    @Override
    public String toString() {
        return "WiFiItemInfo{" +
                "name='" + name + '\'' +
                ", bssid='" + bssid + '\'' +
                ", desc='" + desc + '\'' +
                ", level=" + level +
                ", speed=" + speed +
                ", capabilities='" + capabilities + '\'' +
                ", isLock=" + isLock +
                '}';
    }
}
