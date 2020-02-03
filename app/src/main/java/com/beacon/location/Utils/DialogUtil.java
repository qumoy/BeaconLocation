package com.beacon.location.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.beacon.location.R;

import java.util.Objects;

/**
 * Author Qumoy
 * Create Date 2019/7/29
 * Description：
 * Modifier:
 * Modify Date:
 * Bugzilla Id:
 * Modify Content:
 */
public class DialogUtil {

    /**
     * 创建一个选择对话框
     *
     * @param context
     * @param pContent            提示消息
     * @param dialogClickListener 点击监听
     * @return
     */
    public static Dialog showSelectDialog(Context context, String title, String pContent, String pLeftBtnStr,
                                          String pRightBtnStr,
                                          final DialogClickListener dialogClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setTitle(title)
                .setMessage(pContent)
                .setPositiveButton(pRightBtnStr, (dialog12, which) -> {
                    dialogClickListener.confirm();
                    dialog12.dismiss();
                })
                .setNegativeButton(pLeftBtnStr, (dialog1, which) -> {
                    dialogClickListener.cancel();
                    dialog1.dismiss();
//                        return;
                })
                .create();
        return dialog;
    }


    public interface DialogClickListener {

        void confirm();

        void cancel();

    }

    public interface BeaconDialogClickListener {

        void change(int id, String str);

        void add(Dialog view);

        void back(Dialog view);
    }

    /**
     * 加载添加Beacon弹窗
     */
    public static Dialog showBeaconDialog(Context context, final BeaconDialogClickListener dialogClickListener) {
        Dialog beaconDialog = new Dialog(context, R.style.progress_dialog);
        beaconDialog.setContentView(R.layout.layout_beacon_dialog);
        beaconDialog.setCancelable(false);
        EditText mEdName = beaconDialog.findViewById(R.id.ed_beacon_name);
        EditText mEdX = beaconDialog.findViewById(R.id.ed_beacon_x);
        EditText mEdY = beaconDialog.findViewById(R.id.ed_beacon_y);
        TextView mTvAdd = beaconDialog.findViewById(R.id.tv_add);
        TextView mTvBack = beaconDialog.findViewById(R.id.tv_back);
        mEdName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                dialogClickListener.change(R.id.ed_beacon_name, editable.toString());
            }
        });
        mEdX.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                dialogClickListener.change(R.id.ed_beacon_x, editable.toString());
            }
        });
        mEdY.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                dialogClickListener.change(R.id.ed_beacon_y, editable.toString());
            }
        });
        mTvAdd.setOnClickListener(view -> dialogClickListener.add(beaconDialog));
        mTvBack.setOnClickListener(view -> dialogClickListener.back(beaconDialog));
        beaconDialog.show();
        return beaconDialog;
    }
}
