package com.beacon.location.Beans;


/**
 * 包含蓝牙X、Y坐标，以及minor，以及距离radius
 */
public class Beacon_circle extends Circle_intersection_pos {
    private double pos_x=-1;
    private double pos_y=-1;
    private double radius=9999;
    private int minor=-1;

    Beacon_circle()
    {
    }

    public Beacon_circle(double x, double y, int minor)
    {
        this.pos_x=x;
        this.pos_y=y;
       // this.radius=r;
        this.minor=minor;
    }

    public double get_x()
    {
        return pos_x;
    }
    public double get_y()
    {
        return pos_y;
    }

    public int get_minor() {return this.minor;}
    public double get_r() {return radius;}

    public void set_x(double x)
    {
        this.pos_x=x;
    }
    public void set_y(double y)
    {
        this.pos_y=y;
    }

    public void set_r(double r)
    {
        this.radius=r;
    }



}
