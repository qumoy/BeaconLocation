package com.beacon.location.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

    /**
     * 加载等待弹窗
     */
    public static Dialog showProgressDialog(Context context, String title) {
        Dialog progressDialog = new Dialog(context, R.style.progress_dialog);
        progressDialog.setContentView(R.layout.layout_progress_dialog);
        progressDialog.setCancelable(false);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        TextView msg = progressDialog.findViewById(R.id.id_tv_loadingmsg);
        msg.setText(title);
        progressDialog.show();
        return progressDialog;
    }
}
