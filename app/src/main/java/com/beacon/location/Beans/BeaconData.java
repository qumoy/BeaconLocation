package com.beacon.location.Beans;

/**
 * Author Qumoy
 * Create Date 2020/8/26
 * Description：
 * Modifier:
 * Modify Date:
 * Bugzilla Id:
 * Modify Content:
 */
public class BeaconData {
    public BeaconData(String name, int rssi) {
        this.name = name;
        this.rssi = rssi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    private String name;
    private int rssi;
}
