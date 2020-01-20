package com.beacon.location.Activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.beacon.location.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author XXXXX
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
    @BindView(R.id.ed_beacon_one_x)
    EditText mEdBeaconOneX;
    @BindView(R.id.ed_beacon_one_y)
    EditText mEdBeaconOneY;
    @BindView(R.id.ed_beacon_two_x)
    EditText mEdBeaconTwoX;
    @BindView(R.id.ed_beacon_two_y)
    EditText mEdBeaconTwoY;
    @BindView(R.id.ed_beacon_three_x)
    EditText mEdBeaconThreeX;
    @BindView(R.id.ed_beacon_three_y)
    EditText mEdBeaconThreeY;
    @BindView(R.id.ed_beacon_four_x)
    EditText mEdBeaconFourX;
    @BindView(R.id.ed_beacon_four_y)
    EditText mEdBeaconFourY;
    @BindView(R.id.btn_comfirm)
    Button mBtnComfirm;
    @BindView(R.id.btn_setting_reset)
    Button mBtnReset;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        sp = getSharedPreferences(BEACON_DATA, MODE_PRIVATE);
        mEdBeaconOneX.setText(String.valueOf(sp.getInt(BEACON_ONE_X, 250)));
        mEdBeaconOneY.setText(String.valueOf(sp.getInt(BEACON_ONE_Y, 250)));
        mEdBeaconTwoX.setText(String.valueOf(sp.getInt(BEACON_TWO_X, 750)));
        mEdBeaconTwoY.setText(String.valueOf(sp.getInt(BEACON_TWO_Y, 250)));
        mEdBeaconThreeX.setText(String.valueOf(sp.getInt(BEACON_THREE_X, 250)));
        mEdBeaconThreeY.setText(String.valueOf(sp.getInt(BEACON_THREE_Y, 750)));
        mEdBeaconFourX.setText(String.valueOf(sp.getInt(BEACON_FOUR_X, 750)));
        mEdBeaconFourY.setText(String.valueOf(sp.getInt(BEACON_FOUR_Y, 750)));
    }

    @OnClick({R.id.btn_comfirm, R.id.btn_setting_reset})
    public void onViewClicked(View v) {
        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor edit = sp.edit();
        if (v.getId() == R.id.btn_comfirm) {
            if (!TextUtils.isEmpty(mEdBeaconOneX.getText()) && !TextUtils.isEmpty(mEdBeaconOneY.getText())
                    && !TextUtils.isEmpty(mEdBeaconTwoX.getText()) && !TextUtils.isEmpty(mEdBeaconTwoY.getText())
                    && !TextUtils.isEmpty(mEdBeaconThreeX.getText()) && !TextUtils.isEmpty(mEdBeaconThreeY.getText())
                    && !TextUtils.isEmpty(mEdBeaconFourX.getText()) && !TextUtils.isEmpty(mEdBeaconFourY.getText())) {
                edit.putInt(BEACON_ONE_X, Integer.parseInt(mEdBeaconOneX.getText().toString()));
                edit.putInt(BEACON_ONE_Y, Integer.parseInt(mEdBeaconOneY.getText().toString()));
                edit.putInt(BEACON_TWO_X, Integer.parseInt(mEdBeaconTwoX.getText().toString()));
                edit.putInt(BEACON_TWO_Y, Integer.parseInt(mEdBeaconTwoY.getText().toString()));
                edit.putInt(BEACON_THREE_X, Integer.parseInt(mEdBeaconThreeX.getText().toString()));
                edit.putInt(BEACON_THREE_Y, Integer.parseInt(mEdBeaconThreeY.getText().toString()));
                edit.putInt(BEACON_FOUR_X, Integer.parseInt(mEdBeaconFourX.getText().toString()));
                edit.putInt(BEACON_FOUR_Y, Integer.parseInt(mEdBeaconFourY.getText().toString()));
                edit.apply();
                Toast.makeText(SettingActivity.this, "保存配置成功！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SettingActivity.this, "保存配置失败！", Toast.LENGTH_SHORT).show();
            }
        }
        if (v.getId() == R.id.btn_setting_reset) {
            mEdBeaconOneX.setText(String.valueOf(250));
            mEdBeaconOneY.setText(String.valueOf(250));
            mEdBeaconTwoX.setText(String.valueOf(750));
            mEdBeaconTwoY.setText(String.valueOf(250));
            mEdBeaconThreeX.setText(String.valueOf(250));
            mEdBeaconThreeY.setText(String.valueOf(750));
            mEdBeaconFourX.setText(String.valueOf(750));
            mEdBeaconFourY.setText(String.valueOf(750));
            edit.putInt(BEACON_ONE_X, Integer.parseInt(mEdBeaconOneX.getText().toString()));
            edit.putInt(BEACON_ONE_Y, Integer.parseInt(mEdBeaconOneY.getText().toString()));
            edit.putInt(BEACON_TWO_X, Integer.parseInt(mEdBeaconTwoX.getText().toString()));
            edit.putInt(BEACON_TWO_Y, Integer.parseInt(mEdBeaconTwoY.getText().toString()));
            edit.putInt(BEACON_THREE_X, Integer.parseInt(mEdBeaconThreeX.getText().toString()));
            edit.putInt(BEACON_THREE_Y, Integer.parseInt(mEdBeaconThreeY.getText().toString()));
            edit.putInt(BEACON_FOUR_X, Integer.parseInt(mEdBeaconFourX.getText().toString()));
            edit.putInt(BEACON_FOUR_Y, Integer.parseInt(mEdBeaconFourY.getText().toString()));
            edit.apply();
            Toast.makeText(SettingActivity.this, "恢复默认配置成功！", Toast.LENGTH_SHORT).show();
        }
    }
}
