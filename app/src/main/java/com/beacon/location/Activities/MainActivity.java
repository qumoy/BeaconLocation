package com.beacon.location.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beacon.location.Adapters.BeaconViewAdapter;
import com.beacon.location.Beans.Beacon;
import com.beacon.location.Beans.BeaconData;
import com.beacon.location.Beans.BeaconInfo;
import com.beacon.location.Beans.Beacon_circle;
import com.beacon.location.Dbs.SqlHelper;
import com.beacon.location.Positioning_engine;
import com.beacon.location.R;
import com.beacon.location.Utils.DateUtil;
import com.beacon.location.Utils.DialogUtil;
import com.beacon.location.Utils.ExcelUtil;
import com.beacon.location.Utils.PermissionHelper;
import com.beacon.location.Utils.PermissionInterface;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Author Qumoy
 * Create Date 2020/1/28
 * Description：
 * Modifier:
 * Modify Date:
 * Bugzilla Id:
 * Modify Content:
 */
public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, PermissionInterface {

    private static int beacon_number = 4;
    private String hexScanRecord = "error";
    private int major = -999;
    private int minor = -999;
    private int get_rssi = -999;
    private String get_uuid = "error";
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 2;
    private double dist = 9999;
    private int txPower = -59;    //主设备和从设备时隔1米时的信号强度
    private Beacon[] myIbeacon = new Beacon[(beacon_number + 1)];//有4個 Beacon  //为了表示方便，minor从1开始 myIbeacon[0]没有存储数据

    private float map_x = 0;
    private float map_y = 0;
    private float map_x_1 = -999;
    private float map_y_1 = -999;
    private float map_x_2 = -999;
    private float map_y_2 = -999;
    private float map_x_3 = -999;
    private float map_y_3 = -999;
    private float map_x_4 = -999;
    private float map_y_4 = -999;
    private double user_pos_x = -999;
    private double user_pos_y = -999;
    private int conut_putted_beacons = 0;//计数

    private TextView show_Coordinate;
    private LinearLayout llLayout;
    private Paint p = new Paint();
    private DrawView view;
    private Beacon_circle circle_1;
    private Beacon_circle circle_2;
    private Beacon_circle circle_3;
    private Beacon_circle circle_4;
    private Positioning_engine engine = new Positioning_engine();
    private Button btn_get_serarch;
    private Button btn_get_position;
    private Button btn_reset;
    private Button btn_excel;
    private boolean stop_positioning = true;
    private boolean next_positioning = true;
    private double[] d = new double[5];
    private int[] totaltime = {999, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private Handler mThreadHandler;
    private HandlerThread mThread;
    private PermissionHelper mPermissionHelper;
    private int requestCode;
    private RecyclerView mRc;
    private List<Beacon> mBeaconList = new ArrayList<>();
    private BeaconViewAdapter mBeaconViewAdapter;
    private ArrayList<BeaconData> beaconList = new ArrayList<>();
    private boolean isWriteExcel = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPermissionHelper = new PermissionHelper(this, this);
        requestCode = 1;
        mPermissionHelper.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION});
        //初始化蓝牙
        for (int i = 0; i < beacon_number + 1; i++) {
            myIbeacon[i] = new Beacon("Beacon_999", "error", 1, -999, -59, -999, 9999);//第一个数组位置不用，从索引值为1开始
        }

        Log.v("=====>", "Start onCreate");

        llLayout = findViewById(R.id.map);
        show_Coordinate = findViewById(R.id.show_Coordinate);
        btn_get_serarch = findViewById(R.id.btn_search);  //搜索设备
        btn_get_position = findViewById(R.id.btn_position);  //获取位置
        btn_reset = findViewById(R.id.btn_reset); //复位
        btn_excel = findViewById(R.id.btn_excel); //复位
        mRc = findViewById(R.id.rc);

        //注册监听器
        llLayout.setOnTouchListener(this);
        btn_get_serarch.setOnClickListener(this);
        btn_get_position.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        btn_excel.setOnClickListener(this);

        initRecyclerView();

        initBluetooth();
    }

    /**
     * 初始化蓝牙
     * 若系统蓝牙未打开提示打开蓝牙
     */
    private void initBluetooth() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
            return;
        }
    }

    /**
     * 初始化Recyclerview
     */
    @SuppressLint("WrongConstant")
    private void initRecyclerView() {
        mBeaconViewAdapter = new BeaconViewAdapter(mBeaconList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRc.setLayoutManager(linearLayoutManager);
        mRc.setAdapter(mBeaconViewAdapter);
    }

    /**
     * 刷新RecyclerView中的列表数据
     */
    private void notifyDataSetChanged() {
        if (mBeaconViewAdapter == null) {
            mBeaconViewAdapter = new BeaconViewAdapter(mBeaconList);
            mRc.setAdapter(mBeaconViewAdapter);
        }
        mBeaconViewAdapter.notifyDataSetChanged();
    }

    /**
     * 打开蓝牙并扫描
     */
    public void find_beacon() {
        mBluetoothAdapter.startLeScan(mLeScanCallback);

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
            return;
        }
        /**
         * 在2秒后停止扫描
         */
//        Handler handler = new Handler();
//        handler.postDelayed(() -> mBluetoothAdapter.stopLeScan(mLeScanCallback), (5000));
    }

    /**
     * 扫描设备回调函数  device为扫描结果  Major表示不同区域   Minor表示不同的设备  这里Minor设置为1 2 3 4 5  将从设备属性存入myIbeacon
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = (device, rssi, scanRecord) -> {
        //采集数据
        if (isWriteExcel) {
            BeanconDataCollection(device, rssi, scanRecord);
        }
        //String deviceName=device.getName();    获取从设备硬件名字
        // String deviceAddr = device.getAddress();   获取从设备的MAC地址   //连接从设备
        Log.v("=====>", "Start OnLeScan");
        int startByte = 2;
        boolean patternFound = false;
        while (startByte <= 5) {
            if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                    ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                patternFound = true;
                hexScanRecord = bytesToHex(scanRecord);
                break;
            }
            startByte++;
        }
        Log.v("=====>", " device.getName():" + device.getName());

        if (patternFound) {
            //把id转换为16进制数据
            byte[] uuidBytes = new byte[16];
            System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
            String hexString = bytesToHex(uuidBytes);

            //id进行4个字符拼接
            String uuid = hexString.substring(0, 8) + "-" +
                    hexString.substring(8, 12) + "-" +
                    hexString.substring(12, 16) + "-" +
                    hexString.substring(16, 20) + "-" +
                    hexString.substring(20, 32);

            major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);
            int result = (scanRecord[27 + 1] & 0xff) + (scanRecord[27] & 0xff) * 256;
            Log.e("test", "result: " + result);
            //Minor设置
            if (result == 436 || result == 437 || result == 438) {
                minor = (scanRecord[27 + 1] & 0xff) + (scanRecord[27] & 0xff) * 256 - 435;//事先设备分别设为1,2,3,4,5
                get_uuid = uuid;
                get_rssi = rssi;
                dist = calculateAccuracy(get_rssi);
                Log.e("test", "minor:" + minor + "   dist: " + dist);
                myIbeacon[minor] = new Beacon("Beacon_" + minor, uuid, major, minor, txPower, rssi, dist);  //给每个蓝牙设备赋值
                if (!deviceInfoExists("Beacon_" + minor)) {
                    mBeaconList.add(new Beacon("Beacon_" + minor, uuid, major, minor, txPower, rssi, dist));
                    notifyDataSetChanged();
                } else {
                    Beacon beaconInfo = MainActivity.this.findBeaconInfo(minor);
                    assert beaconInfo != null;
                    beaconInfo.updateParameters(rssi, myIbeacon[minor].get_dist());
                    notifyDataSetChanged();
                }
//                d[minor] = d[minor] + myIbeacon[minor].get_dist();     //便于后面简单求平均
                d[minor] = myIbeacon[minor].get_dist();
                totaltime[minor]++;//每个设备被扫描的次数
                Log.v("=====>", "minor:" + minor);
                Log.v("totaltime", "totaltime" + minor + ";" + totaltime[minor]);
                Log.v("=====>", "RSSI:" + myIbeacon[minor].get_rssi());
                Log.v("totaltime", "distance:" + myIbeacon[minor].get_dist());
            }

        }
    };


    private Beacon findBeaconInfo(int minor) {
        for (int i = 0; i < mBeaconList.size(); i++) {
            if (mBeaconList.get(i).getName()
                    .equals("Beacon_" + minor)) {
                return mBeaconList.get(i);
            }
        }
        return null;
    }

    /**
     * 筛选重复的设备名称
     */
    private boolean deviceInfoExists(String name) {
        for (int i = 0; i < mBeaconList.size(); i++) {
            if (mBeaconList.get(i).getName()
                    .equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 转换算法
     */
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     *   首先是将rssi信号转换为距离：
     *   d=10^((ABS(RSSI)-A)/(10*n))
     *   其中d为距离，单位是m。
     *   RSSI为rssi信号强度，为负数。
     *   A为距离探测设备1m时的rssi值的绝对值，最佳范围在45-49之间。
     *   n为环境衰减因子，需要测试矫正，最佳范围在3.25-4.5之间。
     *
     * @param rssi
     * @return 主设备与从设备之间的距离dist
     */
    protected static double calculateAccuracy(double rssi) {
        if (rssi == 0) {
            return -1.0;
        }
        double absRssi = Math.abs(rssi);
        double power = (absRssi - 65) / (10 * 4.8);
        return Math.pow(10, power);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //初始化Map
        initMap();
    }

    /**
     * 根据存储的Beacon值设置map中Beacon位置
     */
    private void initMap() {
        conut_putted_beacons = 3;
        SqlHelper mSqlHelper = new SqlHelper(this, "Beacon.db", null, 1);
        SQLiteDatabase db = mSqlHelper.getWritableDatabase();
        List<BeaconInfo> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.query("Beacon", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                BeaconInfo beaconInfo = new BeaconInfo();
                beaconInfo.setName(cursor.getString(cursor.getColumnIndex("name")));
                beaconInfo.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                beaconInfo.setX(cursor.getDouble(cursor.getColumnIndex("x")));
                beaconInfo.setY(cursor.getDouble(cursor.getColumnIndex("y")));
                list.add(beaconInfo);
            } while (cursor.moveToNext());
        }
        db.close();//数据库用完关闭
        if (list.size() >= 3) {
            map_x_1 = (float) list.get(0).getX();
            map_y_1 = (float) list.get(0).getY();
            map_x_2 = (float) list.get(1).getX();
            map_y_2 = (float) list.get(1).getY();
            map_x_3 = (float) list.get(2).getX();
            map_y_3 = (float) list.get(2).getY();
//            map_x_4 = (float) list.get(2).getX();
//            map_y_4 = (float) list.get(2).getY();
            put_beacon_and_user_on_map();
        }
    }

    public void infinite_positioning() {
        while (stop_positioning == false) {
            stop_positioning = true;
            next_positioning = true;
            Log.v("=====>", "**************************************开始循环定位");

            if (next_positioning) {
                Log.v("=====>", "Start infinite_positioning if");
                next_positioning = false;

                //线程开启
                if (mThread == null && mThreadHandler == null) {
                    mThread = new HandlerThread("find_beacons");
                    mThread.start();
                    mThreadHandler = new Handler(mThread.getLooper());   //跳转另一个线程
                    mThreadHandler.post(get_user_pos);
                } else {
                    assert mThread != null;
                    mThreadHandler = new Handler(mThread.getLooper());   //跳转另一个线程
                    mThreadHandler.post(get_user_pos);
                }
            }
        }
    }

    /**
     * 开启Runnable获取用户位置
     */
    private Runnable get_user_pos = () -> {
        Log.v("=====>", "Start get_user_pos");

        new Handler().post(() -> {
            // Beacon>2 开始定位
            int find_beacon_number = 0;
            for (int i = 0; i < (beacon_number + 1); i++) {
                if (myIbeacon[i].get_minor() != -999) {
                    Log.v("=====>", "find_beacon_number:" + find_beacon_number);
                    find_beacon_number++;
                }
            }

            if (find_beacon_number > 2) {
                Log.v("=====>", "start engine!");
                for (int i = 1; i < 10; i++) {
                    if (totaltime[i] == 0) continue;
//                    d[i] = d[i] / totaltime[i];     //简单求平均
                    myIbeacon[i].set_dist(d[i]);       //实时算平均
                    Log.e("totaltime", "d[i]: " + d[i]);
                }
                engine.start_positioning(myIbeacon);//运用定位算法求出主设备X，Y坐标
                Log.v("=====>", "user_pos_x:" + engine.get_user_pos().get_x());
                Log.v("=====>", "user_pos_y:" + engine.get_user_pos().get_y());
                user_pos_x = engine.get_user_pos().get_x();   //回调将主设备的坐标赋给user_pos_x
                user_pos_y = engine.get_user_pos().get_y();

                runOnUiThread(() -> {
                    put_beacon_and_user_on_map();
                    Log.v("=====>", "**************************UI更新");

                });
                //程序正常运行结束
            } else {
                Toast.makeText(getApplicationContext(), "beacon少于3个!", Toast.LENGTH_SHORT).show();
            }
//            循环定位
            next_positioning = true;//为下次一开始扫描初始化
            stop_positioning = false;
            for (int i = 0; i < 5; i++) {
                if (i == 0) {
                    d[i] = 999;
                    totaltime[i] = 999;
                } else {
                    d[i] = 0;
                    totaltime[i] = 0;
                }
            }
            for (int i = 0; i < 5; i++) {

                if (i == 0) {
                    totaltime[i] = 999;
                } else {
                    totaltime[i] = 0;
                }
            }
            infinite_positioning();
            Log.v("=====>", "**************************标志位重置进行下一次扫描");

        });
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);  //加载一个布局文件
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //移除
        if (mThreadHandler != null) {
            mThreadHandler.removeCallbacks(get_user_pos);
            Log.v("=====>", "移除移除mThreadHandler!");
        }

        //关闭线程
        if (mThread != null) {
            mThread.quit();
            Log.v("=====>", "移除移除mThread!");
        }
    }


    /*
     * 屏幕按钮事件*/
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                isWriteExcel = true;
                find_beacon();   //寻找设备存入myIbeacon[minor]；
                Toast.makeText(MainActivity.this, "正在搜索设备", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_position:
                Log.v("=====>", "Start btn");
                Toast.makeText(MainActivity.this, "开始定位", Toast.LENGTH_SHORT).show();
                stop_positioning = false;
                infinite_positioning();
                break;
            case R.id.btn_reset:
                llLayout.removeAllViews();
                //view.clearAnimation();
                //view.invalidate();
                conut_putted_beacons = 0;
                Toast.makeText(this, "已清除", Toast.LENGTH_SHORT).show();
                //复位。。。。
                break;
            case R.id.btn_excel:
                isWriteExcel = false;
                Log.e("test2", "beaconList.size(): " + beaconList.size());
                if (beaconList.size() != 0) {
                    new Thread(this::WriteIntoExcel).start();
                }
                break;
        }
    }

    /**
     * 本地EXCEL保存数据
     */


    private void BeanconDataCollection(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.e("test2", "BeanconDataCollection: " + device.getAddress());
        int minor = (scanRecord[27 + 1] & 0xff) + (scanRecord[27] & 0xff) * 256;
        beaconList.add(new BeaconData(String.valueOf(minor), rssi));
    }

    private void WriteIntoExcel() {

        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.Beacon.Location";
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        String data = DateUtil.stampToDate(System.currentTimeMillis());
        String excelFileName = "/" + data + "-BeaconData.xls";
//        String excelFileName = "/BeaconData3.xls";

        String[] title = {"DeviceName", "Rssi"};

        filePath = filePath + excelFileName;

        ExcelUtil.initExcel(filePath, title);

        ExcelUtil.writeObjListToExcel(beaconList, filePath, getApplicationContext());

    }

    /**
     * @param v
     * @param event 点击从设备位置X，Y坐标，并在界面显示出来
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        map_x = event.getX();    //得到界面点X、Y坐标
        map_y = event.getY();
        conut_putted_beacons++;//记录点击点数
        show_Coordinate.setText("X: " + map_x / 500 + ", Y: " + map_y / 500);

        if (conut_putted_beacons == 1) {
            map_x_1 = map_x;
            map_y_1 = map_y;
        } else if (conut_putted_beacons == 2) {
            map_x_2 = map_x;
            map_y_2 = map_y;
        } else if (conut_putted_beacons == 3) {
            map_x_3 = map_x;
            map_y_3 = map_y;
        } else if (conut_putted_beacons == 4) {
            map_x_4 = map_x;
            map_y_4 = map_y;
        } else {
            Toast.makeText(getApplicationContext(), "有n个beacon就要设置n个位置", Toast.LENGTH_SHORT).show();
        }

        put_beacon_and_user_on_map();
        return false;
    }

    public void put_beacon_and_user_on_map() {
        Log.v("=====>", "Start put_beacon_and_user_on_map");

        if (view == null) {
            view = new DrawView(this);
            view.setMinimumHeight(1000);
            view.setMinimumWidth(1000);
            view.invalidate();//此方法是在iu线程中使用，实现界面刷新。
            llLayout.addView(view, 0);
        } else {
            view.invalidate();
        }

    }


    /**
     * 自定义view类
     */
    public class DrawView extends View {

        public DrawView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);//Canvas类定义了绘制文本，线条，图像等一系列方法
            //绘制什么，由Canvas控制    如何绘制，由Paint控制
            // 建立初始畫布             //Paint p = new Paint();	前面已经命名了
            // 创建画笔
            draw_user_and_beacon_position(canvas);
        }

        private void draw_user_and_beacon_position(Canvas canvas) {

            p.setAntiAlias(true);                                    // 設置畫筆的锯齿效果。 true是去除。

            p.setColor(Color.BLUE);                                // 设置颜色

            if (conut_putted_beacons == 1) {
                canvas.drawCircle(map_x_1, map_y_1, 20, p);
            } else if (conut_putted_beacons == 2) {
                canvas.drawCircle(map_x_1, map_y_1, 20, p);
                canvas.drawCircle(map_x_2, map_y_2, 20, p);
            } else if ((conut_putted_beacons == 3)) {
                canvas.drawCircle(map_x_1, map_y_1, 20, p);
                canvas.drawCircle(map_x_2, map_y_2, 20, p);
                canvas.drawCircle(map_x_3, map_y_3, 20, p);
                assign_radius_and_set_circles(conut_putted_beacons);
                draw_user(canvas);
            } else if ((conut_putted_beacons == 4)) {
                canvas.drawCircle(map_x_1, map_y_1, 20, p);
                canvas.drawCircle(map_x_2, map_y_2, 20, p);
                canvas.drawCircle(map_x_3, map_y_3, 20, p);
                canvas.drawCircle(map_x_4, map_y_4, 20, p);
                assign_radius_and_set_circles(conut_putted_beacons);
                draw_user(canvas);
            }
        }

    }

    public void assign_radius_and_set_circles(int conut) {
        /**
         * For circle_n
         * 至少三个才能定位
         * Beacons need to be putted in order.  将转换过来的X和Y赋给蓝牙，蓝牙命名为1,2,3,4,5
         * Such as   1.minor=3      2.minor=4      3.minor=5  ,etc
         */
        if (conut == 3) {
            circle_1 = new Beacon_circle(map_x_1, map_y_1, 1);
            circle_2 = new Beacon_circle(map_x_2, map_y_2, 2);
            circle_3 = new Beacon_circle(map_x_3, map_y_3, 3);
            engine.set_circles(circle_1, circle_2, circle_3);
        } else if (conut == 4) {
            circle_1 = new Beacon_circle(map_x_1, map_y_1, 1);
            circle_2 = new Beacon_circle(map_x_2, map_y_2, 2);
            circle_3 = new Beacon_circle(map_x_3, map_y_3, 3);
            circle_4 = new Beacon_circle(map_x_4, map_y_4, 4);
            engine.set_circles(circle_1, circle_2, circle_3, circle_4);
        }
    }

    @SuppressLint("SetTextI18n")
    public void draw_user(Canvas canvas) {
        if ((user_pos_x >= 0) && (user_pos_y >= 0)) {
            p.setColor(Color.RED);
            canvas.drawCircle((float) (user_pos_x), (float) (user_pos_y), 20, p);
            DecimalFormat df = new DecimalFormat("0.00");
            show_Coordinate.setText("用户位置X: " + df.format(user_pos_x / 500) + ", Y: " + df.format(user_pos_y / 500));
        }
    }

    @Override
    public int getPermissionsRequestCode() {
        return requestCode;
    }

    @Override
    public void requestPermissionsSuccess() {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void requestPermissionsFail() {
        if (requestCode == 1) {
            //如果拒绝授予权限,且勾选了再也不提醒
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                DialogUtil.showSelectDialog(this, "说明", "需要使用位置权限，进行蓝牙操作", "取消", "确定", new DialogUtil.DialogClickListener() {
                    @Override
                    public void confirm() {
                        //用于在用户勾选“不再提示”并且拒绝时，再次提示用户
                        DialogUtil.showSelectDialog(MainActivity.this, "位置权限不可用", "请在-应用设置-权限中，允许APP使用位置权限", "取消", "立即开启", new DialogUtil.DialogClickListener() {
                            @Override
                            public void confirm() {
                                goToAppSetting();
                            }

                            @Override
                            public void cancel() {

                            }
                        }).show();
                    }

                    @Override
                    public void cancel() {

                    }

                }).show();
            } else {
                DialogUtil.showSelectDialog(MainActivity.this, "位置权限不可用", "请在-应用设置-权限中，允许APP使用位置权限", "取消", "立即开启", new DialogUtil.DialogClickListener() {
                    @Override
                    public void confirm() {
                        goToAppSetting();
                    }

                    @Override
                    public void cancel() {

                    }

                }).show();
            }
            //如果拒绝授予权限,且勾选了再也不提醒
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                DialogUtil.showSelectDialog(this, "说明", "需要使用位置权限，进行蓝牙操作", "取消", "确定", new DialogUtil.DialogClickListener() {
                    @Override
                    public void confirm() {
                        //用于在用户勾选“不再提示”并且拒绝时，再次提示用户
                        DialogUtil.showSelectDialog(MainActivity.this, "位置权限不可用", "请在-应用设置-权限中，允许APP使用位置权限", "取消", "立即开启", new DialogUtil.DialogClickListener() {
                            @Override
                            public void confirm() {
                                goToAppSetting();
                            }

                            @Override
                            public void cancel() {

                            }


                        }).show();
                    }

                    @Override
                    public void cancel() {

                    }


                }).show();
            } else {
                DialogUtil.showSelectDialog(MainActivity.this, "位置权限不可用", "请在-应用设置-权限中，允许APP使用位置权限", "取消", "立即开启", new DialogUtil.DialogClickListener() {
                    @Override
                    public void confirm() {
                        goToAppSetting();
                    }

                    @Override
                    public void cancel() {

                    }


                }).show();
            }
            //如果拒绝授予权限,且勾选了再也不提醒
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                DialogUtil.showSelectDialog(this, "说明", "需要使用位置权限，进行蓝牙操作", "取消", "确定", new DialogUtil.DialogClickListener() {
                    @Override
                    public void confirm() {
                        //用于在用户勾选“不再提示”并且拒绝时，再次提示用户
                        DialogUtil.showSelectDialog(MainActivity.this, "位置权限不可用", "请在-应用设置-权限中，允许APP使用位置权限", "取消", "立即开启", new DialogUtil.DialogClickListener() {
                            @Override
                            public void confirm() {
                                goToAppSetting();
                            }

                            @Override
                            public void cancel() {

                            }


                        }).show();
                    }

                    @Override
                    public void cancel() {

                    }


                }).show();
            } else {
                DialogUtil.showSelectDialog(MainActivity.this, "写入权限不可用", "请在-应用设置-权限中，允许APP使用位置权限", "取消", "立即开启", new DialogUtil.DialogClickListener() {
                    @Override
                    public void confirm() {
                        goToAppSetting();
                    }

                    @Override
                    public void cancel() {

                    }


                }).show();
            }
        }
    }

    /**
     * 打开Setting
     */
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 123);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)) {
            //权限请求结果，并已经处理了该回调
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            //Todo show setting success
        }

    }

}
