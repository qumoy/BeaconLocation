package com.beacon.location.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beacon.location.Adapters.BeaconLocationViewAdapter;
import com.beacon.location.Beans.BeaconInfo;
import com.beacon.location.Dbs.SqlHelper;
import com.beacon.location.R;
import com.beacon.location.Utils.DialogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author Qumoy
 * Create Date 2020/1/18
 * Description：
 * Modifier:
 * Modify Date:
 * Bugzilla Id:
 * Modify Content:
 */
public class SettingActivity extends AppCompatActivity {
    protected static final String BEACON_DATA = "BEACON";
    protected static final String BEACON_ONE_X = "BEACON_ONE_X";
    protected static final String BEACON_ONE_Y = "BEACON_ONE_Y";
    protected static final String BEACON_TWO_X = "BEACON_TWO_X";
    protected static final String BEACON_TWO_Y = "BEACON_TWO_Y";
    protected static final String BEACON_THREE_X = "BEACON_THREE_X";
    protected static final String BEACON_THREE_Y = "BEACON_THREE_Y";
    protected static final String BEACON_FOUR_X = "BEACON_FOUR_X";
    protected static final String BEACON_FOUR_Y = "BEACON_FOUR_Y";
    @BindView(R.id.rc_beacon_location)
    RecyclerView mRc;
    private SqlHelper mSqlHelper;
    private String mBeaconName = "Beacon_1";
    private String mBeaconNameDelete;
    private double mBeaconX = 0;
    private double mBeaconY = 0;
    private BeaconLocationViewAdapter mAdapter;
    private List<BeaconInfo> mBeaconList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        //初始化数据库
        mSqlHelper = new SqlHelper(this, "Beacon.db", null, 1);
        //初始化RecyclerView
        mRc.setLayoutManager(new LinearLayoutManager(SettingActivity.this));
        mBeaconList = queryBeaconDb();
        mAdapter = new BeaconLocationViewAdapter(mBeaconList);
        mAdapter.setOnItemClickListener(pos -> {
            showDialog("是否要清除该条数据?", pos);
        });
        mRc.setAdapter(mAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);  //加载一个布局文件
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.setting_add) {
            DialogUtil.showBeaconDialog(this, new DialogUtil.BeaconDialogClickListener() {

                @Override
                public void change(int id, String s) {
                    switch (id) {
                        case R.id.ed_beacon_name:
                            mBeaconName = s;
//                            Toast.makeText(SettingActivity.this, s, Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.ed_beacon_x:
                            if (!TextUtils.isEmpty(s)) {
                                mBeaconX = Double.parseDouble(s);
//                                Toast.makeText(SettingActivity.this, s, Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case R.id.ed_beacon_y:
                            if (!TextUtils.isEmpty(s)) {
                                mBeaconY = Double.parseDouble(s);
//                                Toast.makeText(SettingActivity.this, s, Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                }

                @Override
                public void add(Dialog view) {
                    insertBeaconDb();
                    if (mAdapter != null) {
                        mBeaconList = queryBeaconDb();
                        mAdapter.setBeaconList(mBeaconList);
                        mAdapter.notifyDataSetChanged();
                    }
                    view.dismiss();
                }

                @Override
                public void back(Dialog view) {
                    view.dismiss();
                }
            });
        }
        if (id == R.id.setting_delete) {
            if (!TextUtils.isEmpty(mBeaconNameDelete)) {
                deleteBeaconDb(mBeaconNameDelete);
                if (mAdapter != null) {
                    mAdapter.setBeaconList(queryBeaconDb());
                    mAdapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(getApplicationContext(), "请选中要删除的Beacon信息", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 写入数据库
     */
    public void insertBeaconDb() {
        SQLiteDatabase db = mSqlHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", mBeaconName);
        values.put("uuid", 1234);
        values.put("x", mBeaconX);
        values.put("y", mBeaconY);
        long insert = db.insert("Beacon", null, values);
        if (insert > 0) {
            Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_SHORT).show();
        }
        db.close();//数据库用完关闭
    }

    /**
     * 查询数据库
     */
    public List<BeaconInfo> queryBeaconDb() {
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
        return list;
    }

    /**
     * 删除数据库
     */
    public void deleteBeaconDb(String name) {
        SQLiteDatabase db = mSqlHelper.getWritableDatabase();
        int delete = db.delete("Beacon", "name=?", new String[]{String.valueOf(name)});
        if (delete > 0) {
            Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "删除失败", Toast.LENGTH_SHORT).show();
        }
        db.close();//数据库用完关闭
    }

    /**
     * 消息弹框设置
     *
     * @param title 设置弹窗标题
     */
    public void showDialog(String title, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setNegativeButton(getResources().getString(R.string.text_negative_button), (dialog, i) -> dialog.dismiss())
                .setPositiveButton(getResources().getString(R.string.text_positive_button), (dialog, which) -> {
                    deleteBeaconDb(mBeaconList.get(pos).getName());
                    mBeaconList.remove(pos);
                    mAdapter.setBeaconList(mBeaconList);
                    mAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
