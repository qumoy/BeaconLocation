package com.beacon.location;


import android.util.Log;
import com.beacon.location.Beans.Beacon;
import com.beacon.location.Beans.Beacon_circle;
import com.beacon.location.Beans.Circle_intersection_pos;
import com.beacon.location.Beans.Cross_pos_and_dist;

import java.util.Arrays;


public class Positioning_engine {

    private static int beacon_number = 4;
    private Beacon[] myIbeacon = new Beacon[(beacon_number + 1)];//有4個 Beacon
    private Beacon_circle circle_1;   //存储三条最短的距离的设备
    private Beacon_circle circle_2;
    private Beacon_circle circle_3;
    private Beacon_circle[] circles = new Beacon_circle[4];
    Beacon_circle[] sorted_circle_first3 = new Beacon_circle[3];
    private Circle_intersection_pos sect_pos[] = new Circle_intersection_pos[6];    //存储三个最短路径圆的交点
    private Circle_intersection_pos last_time_sect_pos[] = new Circle_intersection_pos[6];
    private Circle_intersection_pos pos_user;
    private Cross_pos_and_dist[] pos_dist_combine = new Cross_pos_and_dist[15];
    private Cross_pos_and_dist[] nearest_combine = new Cross_pos_and_dist[3];
    private Circle_intersection_pos critical_cross_point[] = new Circle_intersection_pos[3];


    public Positioning_engine()//constructor
    {
    }

    public void set_circles(Beacon_circle circle1, Beacon_circle circle2, Beacon_circle circle3) {
        this.circles[0] = circle1;
        this.circles[1] = circle2;
        this.circles[2] = circle3;
    }

    public void set_circles(Beacon_circle circle1, Beacon_circle circle2, Beacon_circle circle3, Beacon_circle circle4) {
        this.circles[0] = circle1;
        this.circles[1] = circle2;
        this.circles[2] = circle3;
        this.circles[3] = circle4;
    }

    public void start_positioning(Beacon[] ibeacon) {
        this.myIbeacon = ibeacon;
        put_correct_radius_to_circles();   //将设备所求距离存储在响应设备里并缩小100
        Log.v("=====>", "put_correct_radius_to_circles() finish!");
        beacon_circle_sort(circles);     //将距离从小到大排序并将最短的前3个距离赋给sorted_circle_first3[]
        Log.v("=====>", "beacon_circle_sort(circles); finish!");
        circle_1 = sorted_circle_first3[0];
        circle_2 = sorted_circle_first3[1];
        circle_3 = sorted_circle_first3[2];
        Log.v("=====>", "circle_3 = sorted_circle[2] finish!");
        calc_all_cross_points(circle_1, circle_2, circle_3);   //求出三个圆的交点
        Log.v("=====>", "calc_all_cross_points(circle_1,circle_2,circle_3); finish!");
        get_critical_3_cross_points_and_user_position();  //求出设备坐标位置
        Log.v("=====>", "get_critical_3_cross_points_and_user_position(); finish!");
    }

    /**
     * 将设备距离存储在响应的界面设备里并进行转换
     */
    public void put_correct_radius_to_circles() {
        //Assign radius to corresponding beacons(according to minor number).
        for (int i = 1; i < beacon_number + 1; i++) {
            // Log.v("=====>", "circles[i].get_minor() : "+circles[i].get_minor());
            // Log.v("=====>", "myIbeacon[i].get_minor()) : "+myIbeacon[i].get_minor() );
            if (circles[0].get_minor() == myIbeacon[i].get_minor()) {
                circles[0].set_r(myIbeacon[i].get_dist());
                Log.v("test", "circles[0].get_r() : " + circles[0].get_r());

            } else if (circles[1].get_minor() == myIbeacon[i].get_minor()) {
                circles[1].set_r(myIbeacon[i].get_dist());
                Log.v("test", "circles[1].get_r() : " + circles[1].get_r());

            } else if (circles[2].get_minor() == myIbeacon[i].get_minor()) {
                circles[2].set_r(myIbeacon[i].get_dist());
                Log.v("test", "circles[2].get_r() : " + circles[2].get_r());

            } else if (circles[3].get_minor() == myIbeacon[i].get_minor()) {
                circles[3].set_r(myIbeacon[i].get_dist());
                Log.v("test", "circles[3].get_r() : " + circles[3].get_r());

            }

        }
        //得到的距离扩大1000倍
        for (int i = 0; i < beacon_number; i++) {
            this.circles[i].set_r((circles[i].get_r() * 1200));
        }
    }


    public void calc_all_cross_points(Beacon_circle A, Beacon_circle B, Beacon_circle C) {
        calc_cirlce_cross_points(A, B, 0);
        calc_cirlce_cross_points(B, C, 2);
        calc_cirlce_cross_points(A, C, 4);
    }


    public void calc_cirlce_cross_points(Beacon_circle A, Beacon_circle B, int store_index) {

        double x1 = A.get_x();
        double y1 = A.get_y();
        double r1 = A.get_r();
        Log.e("test", "A.get_x(): " + A.get_x() + "  A.get_y(): " + A.get_y() + "  A.get_r():" + A.get_r());
        double x2 = B.get_x();
        double y2 = B.get_y();
        double r2 = B.get_r();
        Log.e("test", "B.get_x(): " + B.get_x() + "  B.get_y(): " + B.get_y() + "  B.get_r():" + B.get_r());
        double sect_x1 = -1;//交点坐标
        double sect_y1 = -1;
        double sect_x2 = -1;
        double sect_y2 = -1;

        if (y1 != y2)//两个圆心不同时
        {//m= y=mx+k的x项系数、k= y=mx+k的k项系数、 a、b、c= x=(-b±√(b^2-4ac))/2a的系数
            double m = (x1 - x2) / (y2 - y1), k = (Math.pow(r1, 2) - Math.pow(r2, 2) + Math.pow(y2, 2) - Math.pow(y1, 2) + Math.pow(x2, 2) - Math.pow(x1, 2)) / (2 * (y2 - y1));
            double a = 1 + Math.pow(m, 2), b = 2 * (k * m - x2 - m * y2), c = Math.pow(x2, 2) + Math.pow(y2, 2) + Math.pow(k, 2) - 2 * k * y2 - Math.pow(r2, 2);

            if (b * b - 4 * a * c >= 0)//有交点时
            {
                sect_x1 = ((-b) + Math.sqrt(b * b - 4 * a * c)) / (2 * a);//x=(-b+√(b^2-4ac))/2a
                sect_y1 = m * sect_x1 + k;//y=mx+k
                sect_x2 = ((-b) - Math.sqrt(b * b - 4 * a * c)) / (2 * a);//x=(-b-√(b^2-4ac))/2a
                sect_y2 = m * sect_x2 + k;//y=mx+k
                if (b * b - 4 * a * c > 0)//有交点且有两个交点时
                {
                    sect_pos[store_index] = new Circle_intersection_pos(sect_x1, sect_y1);
                    sect_pos[(store_index + 1)] = new Circle_intersection_pos(sect_x2, sect_y2);

                    //当信号不稳定时，存储起来
                    last_time_sect_pos[store_index] = new Circle_intersection_pos(sect_x1, sect_y1);
                    last_time_sect_pos[(store_index + 1)] = new Circle_intersection_pos(sect_x2, sect_y2);
                    Log.v("test", "A.get_minor():" + A.get_minor() + "  B.get_minor():" + B.get_minor() + "多个交点！！！");
                    Log.v("=====>", "sect_pos" + store_index + ": ( " + sect_pos[store_index].get_x() + " , " + sect_pos[store_index].get_y() + " )");
                    Log.v("=====>", "sect_pos" + (store_index + 1) + ": ( " + sect_pos[(store_index + 1)].get_x() + " , " + sect_pos[(store_index + 1)].get_y() + " )");
                } else//有交点且只有一个交点时
                {
                    sect_pos[store_index] = new Circle_intersection_pos(sect_x1, sect_y1);
                    sect_pos[(store_index + 1)] = new Circle_intersection_pos(sect_x1, sect_y1);
                    //Toast.makeText(getApplicationContext(), "只有1個交點！!!!!!!!", Toast.LENGTH_SHORT).show();
                    Log.v("test", "A.get_minor():" + A.get_minor() + "   B.get_minor():" + B.get_minor() + "只有1個交點！!!!!!!!");
                    //Log.v("=====>", "sect_pos"+store_index": ( "+sect_pos[store_index].get_x()+" , "+sect_pos[store_index].get_y()+" )");
                    //Log.v("=====>", "sect_pos"+(store_index+1)": ( "+sect_pos[(store_index+1)].get_x()+" , "+sect_pos[(store_index+1)].get_y()+" )");
                }
            } else//沒有交点时
            {
                sect_pos[store_index] = new Circle_intersection_pos();
                sect_pos[(store_index + 1)] = new Circle_intersection_pos();
                //Toast.makeText(getApplicationContext(), "沒有交點!!!!!", Toast.LENGTH_SHORT).show();
                Log.v("test", "A.get_minor():" + A.get_minor() + "   B.get_minor():" + B.get_minor() + "沒有交點！!!!!!!!");

            }
        } else if ((y1 == y2))//两圆圆心中，Y坐标相等时
        {   //sect_x1= 两圆心交点X坐标值、 a、b、c= x=(-b±√(b^2-4ac))/2a的系数
            sect_x1 = (Math.pow(x2, 2) - Math.pow(x1, 2) + Math.pow(r1, 2) - Math.pow(r2, 2)) / (2 * (x2 - x1));
            double a = 1, b = -2 * y1, c = Math.pow(sect_x1, 2) + Math.pow(x1, 2) - 2 * x1 * sect_x1 + Math.pow(y1, 2) - Math.pow(r1, 2);
            if (b * b - 4 * a * c >= 0) {
                sect_y1 = ((-b) + Math.sqrt(b * b - 4 * a * c)) / (2 * a);//y=(-b+√(b^2-4ac))/2a
                sect_y2 = ((-b) - Math.sqrt(b * b - 4 * a * c)) / (2 * a);//y=(-b-√(b^2-4ac))/2a
                if (b * b - 4 * a * c > 0)//两交点
                {
                    sect_pos[store_index] = new Circle_intersection_pos(sect_x1, sect_y1);
                    sect_pos[(store_index + 1)] = new Circle_intersection_pos(sect_x1, sect_y2);

                    last_time_sect_pos[store_index] = new Circle_intersection_pos(sect_x1, sect_y1);
                    last_time_sect_pos[(store_index + 1)] = new Circle_intersection_pos(sect_x1, sect_y2);

                    Log.v("=====>", "sect_pos" + store_index + ": ( " + sect_pos[store_index].get_x() + " , " + sect_pos[store_index].get_y() + " )");
                    Log.v("=====>", "sect_pos" + (store_index + 1) + ": ( " + sect_pos[(store_index + 1)].get_x() + " , " + sect_pos[(store_index + 1)].get_y() + " )");
                } else//一交点
                {
                    sect_pos[store_index] = new Circle_intersection_pos(sect_x1, sect_y1);
                    sect_pos[(store_index + 1)] = new Circle_intersection_pos(sect_x1, sect_y1);
                    Log.v("=====>", "only one cross point,remain the same 2 cross point!");
                }
            } else//沒有交点时
            {
                sect_pos[store_index] = new Circle_intersection_pos();
                sect_pos[(store_index + 1)] = new Circle_intersection_pos();
                Log.v("=====>", "两圆Y值相同时，沒有交點！!!!!!!!");
            }
        }
    }

    public void get_critical_3_cross_points_and_user_position() {
        for (int i = 0; i < 6; i++) {
            try {
                if ((sect_pos[i].get_x() == -9999) && (sect_pos[i].get_y() == -9999)) {
                    sect_pos[i] = new Circle_intersection_pos(last_time_sect_pos[i].get_x(), last_time_sect_pos[i].get_y());//如果
                }
            } catch (Exception ex) {
                Log.v("=====>", "The cross point is too less!");
                Log.v("=====>", "Prediction,due to the available data is too less");

                double predict_x = ((sorted_circle_first3[0].get_x() + sorted_circle_first3[1].get_x() + sorted_circle_first3[2].get_x()) / 3);
                double predict_y = ((sorted_circle_first3[0].get_y() + sorted_circle_first3[1].get_y() + sorted_circle_first3[2].get_y()) / 3);
                sect_pos[i] = new Circle_intersection_pos(predict_x, predict_y);    //如果抛出异常  选取最小的三个圆，圆心平均值最为交点

            }
        }


        pos_dist_combine[0] = new Cross_pos_and_dist(sect_pos[0], sect_pos[1]);// 6个交点15种连接
        pos_dist_combine[1] = new Cross_pos_and_dist(sect_pos[0], sect_pos[2]);
        pos_dist_combine[2] = new Cross_pos_and_dist(sect_pos[0], sect_pos[3]);
        pos_dist_combine[3] = new Cross_pos_and_dist(sect_pos[0], sect_pos[4]);
        pos_dist_combine[4] = new Cross_pos_and_dist(sect_pos[0], sect_pos[5]);
        pos_dist_combine[5] = new Cross_pos_and_dist(sect_pos[1], sect_pos[2]);
        pos_dist_combine[6] = new Cross_pos_and_dist(sect_pos[1], sect_pos[3]);
        pos_dist_combine[7] = new Cross_pos_and_dist(sect_pos[1], sect_pos[4]);
        pos_dist_combine[8] = new Cross_pos_and_dist(sect_pos[1], sect_pos[5]);
        pos_dist_combine[9] = new Cross_pos_and_dist(sect_pos[2], sect_pos[3]);
        pos_dist_combine[10] = new Cross_pos_and_dist(sect_pos[2], sect_pos[4]);
        pos_dist_combine[11] = new Cross_pos_and_dist(sect_pos[2], sect_pos[5]);
        pos_dist_combine[12] = new Cross_pos_and_dist(sect_pos[3], sect_pos[4]);
        pos_dist_combine[13] = new Cross_pos_and_dist(sect_pos[3], sect_pos[5]);
        pos_dist_combine[14] = new Cross_pos_and_dist(sect_pos[4], sect_pos[5]);

        cross_pos_and_dist_sort(pos_dist_combine);

        for (int i = 0; i < 3; i++) {
            nearest_combine[i] = pos_dist_combine[i];//选取距离最短的三个组合
        }

        unique_pos(nearest_combine);//得到三个交点
        calc_where_is_user(critical_cross_point);//get user position:pos_user

    }


    public void cross_pos_and_dist_sort(Cross_pos_and_dist[] values) {
        double[] c = new double[15];
        if (values == null || values.length == 0) {
            Log.v("=====>", "**************values为空******************");

        } else {
            for (int i = 0; i < values.length; i++) {
                c[i] = values[i].get_dist();
            }
            Arrays.sort(c);
            for (int i = 0; i < values.length; i++) {
                if (values[i].get_dist() == c[0]) {
                    pos_dist_combine[0] = values[i];
                }
                if (values[i].get_dist() == c[1]) {
                    pos_dist_combine[1] = values[i];
                }
                if (values[i].get_dist() == c[2]) {
                    pos_dist_combine[2] = values[i];
                }
            }

        }
    }

    public void unique_pos(Cross_pos_and_dist[] A)  //choose 3 unique position from 6 position made of 3 same combination
    {
        Circle_intersection_pos B[] = new Circle_intersection_pos[6];
        B[0] = new Circle_intersection_pos(A[0].get_x1(), A[0].get_y1());  //两个交点相连最短距离其中一个交点
        B[1] = new Circle_intersection_pos(A[0].get_x2(), A[0].get_y2());
        B[2] = new Circle_intersection_pos(A[1].get_x1(), A[1].get_y1());
        B[3] = new Circle_intersection_pos(A[1].get_x2(), A[1].get_y2());
        B[4] = new Circle_intersection_pos(A[2].get_x1(), A[2].get_y1());
        B[5] = new Circle_intersection_pos(A[2].get_x2(), A[2].get_y2());


        int w = 0;

        for (int i = 0; i < 6; i++) {
            int z = 0;
            while (((i + z) < 6)) {
                if ((B[i].get_x() == B[i + z].get_x()) && (B[i].get_y() == B[i + z].get_y()))//找出交点相同的两点
                {

                    try {
                        critical_cross_point[w] = B[i];//这里防止有多余三个交点相同的情况
                        w++;
                        z++;
                    } catch (Exception ex) {
                        Log.v("=====>", "Wrong Prediction!");
                        Log.v("=====>", ex.getMessage());
                    }
                    break;
                } else {
                    z++;
                }
            }

        }
    }

    public void calc_where_is_user(Circle_intersection_pos[] A) {

        Log.v("test", "Start calc_where_is_user:");
        try {
            pos_user = new Circle_intersection_pos(((A[0].get_x() + A[1].get_x() + A[2].get_x()) / 3), ((A[0].get_y() + A[1].get_y() + A[2].get_y()) / 3));
            Log.v("test", "pos_user：" + pos_user.get_x() + "    " + pos_user.get_y());
        } catch (Exception ex) {
            Log.v("=====>", ex.getMessage());
        }
    }

    public Circle_intersection_pos get_user_pos() {
        return pos_user;
    }
//=================================================================================================


    public void beacon_circle_sort(Beacon_circle[] values) {

        double[] c = new double[4];
        if (values == null || values.length == 0) {
            Log.v("=====>", "**************values为空******************");

            //  return;
        } else {
            for (int i = 0; i < values.length; i++) {
                c[i] = values[i].get_r();
            }
            Arrays.sort(c);
            for (int i = 0; i < values.length; i++) {
                if (values[i].get_r() == c[0]) {
                    sorted_circle_first3[0] = values[i];
                }
                if (values[i].get_r() == c[1]) {
                    sorted_circle_first3[1] = values[i];
                }
                if (values[i].get_r() == c[2]) {
                    sorted_circle_first3[2] = values[i];
                }
            }

        }
    }

}
