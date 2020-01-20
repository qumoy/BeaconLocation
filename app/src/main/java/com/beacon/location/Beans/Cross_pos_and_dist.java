package com.beacon.location.Beans;


/**
 * 存储两交点，并求出距离存入distance
 */
public class Cross_pos_and_dist {

    private double pos_x1=-1;
    private double pos_y1=-1;
    private double pos_x2=-1;
    private double pos_y2=-1;
    private double distance=9999;

    public Cross_pos_and_dist(Circle_intersection_pos A, Circle_intersection_pos B)
    {
        pos_x1=A.get_x();
        pos_y1=A.get_y();
        pos_x2=B.get_x();
        pos_y2=B.get_y();

        this.distance=get_2_points_distance(A,B);

    }

    private double get_2_points_distance(Circle_intersection_pos A, Circle_intersection_pos B)
    {
        double x1=A.get_x();
        double y1=A.get_y();
        double x2=B.get_x();
        double y2=B.get_y();

        double dist = Math.sqrt(  (  (Math.pow((x1-x2),2))  +  (Math.pow((y1-y2),2))  )  );
        return dist;
    }

    public double get_x1()
    {
        return  pos_x1;
    }

    public double get_y1()
    {
        return  pos_y1;
    }
    public double get_x2()
    {
        return  pos_x2;
    }

    public double get_y2()
    {
        return  pos_y2;
    }

    public double get_dist()
    {
        return  distance;
    }

}
