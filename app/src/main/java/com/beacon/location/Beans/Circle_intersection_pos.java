package com.beacon.location.Beans;

/**
 * Author Qumoy
 * Create Date 2020/1/18
 * Description：
 * Modifier:
 * Modify Date:
 * Bugzilla Id:
 * Modify Content:
 */
/**
 * 包含X，Y坐标
 */
public class Circle_intersection_pos {

    private double pos_x=-9999;
    private double pos_y=-9999;

    public Circle_intersection_pos()
    {
        this.pos_x = -9999;
        this.pos_y = -9999;
    }

    public Circle_intersection_pos(double x, double y)
    {
        this.pos_x=x;
        this.pos_y=y;
    }

    public double get_x()
    {
        return pos_x;
    }

    public double get_y()
    {
        return pos_y;
    }

    public void set_x(double x)
    {
        this.pos_x=x;
    }

    public void set_y(double y)
    {
        this.pos_y=y;
    }

}
