package com.beacon.location.Beans;

/**
 * 创建beacon数据项类
 */
public class Beacon {
    private static final int REQUEST_ENABLE_BT = 2;
    private String name;
    private double dist = 9999;    //设备与模块之间距离
    private int txPower = -59;
    private int major = -999;
    private int minor = -999;
    private int rssi = -999;
    private String uuid = "error";
    private double[] a = {1, 2};

    public Beacon(String Name, String Uuid, int Major, int Minor, int TxPower, int Rssi, double Distance) {
        this.name = Name;
        this.uuid = Uuid;
        this.major = Major;
        this.minor = Minor;
        this.txPower = TxPower;
        this.rssi = Rssi;
        this.dist = Distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String get_uuid() {
        return this.uuid;
    }

    public int get_major() {
        return this.major;
    }

    public int get_minor() {
        return this.minor;
    }

    public int get_txPower() {
        return this.txPower;
    }

    public int get_rssi() {
        return this.rssi;
    }

    public double get_dist() {
        return this.dist;
    }

    public void set_dist(double d) {
        this.dist = d;
    }

    public void updateParameters(int rssi, double dist) {
        this.rssi = rssi;
        this.dist = dist;
    }
}
