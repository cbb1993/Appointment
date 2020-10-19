package com.huanhong.appointment.views;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.huanhong.appointment.R;

/**
 * Created by 坎坎.
 * Date: 2019/6/6
 * Time: 11:32
 * describe:
 */
public class SuccessDialog extends Dialog {
    public SuccessDialog(Context context,String content ,ConfirmCallback callback) {
        super(context, R.style.app_dialog);
        init(content,callback);
    }
    private void init(String content, final ConfirmCallback callback) {
        setContentView(R.layout.dialog_success);
        setCanceledOnTouchOutside(false);
        if (getWindow() != null) {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            if (attr != null) {
                attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                attr.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                attr.gravity = Gravity.CENTER;//设置dialog 在布局中的位置
            }
        }
        findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(callback!=null){
                    callback.confirm();
                }
            }
        });
        TextView tv_remark = findViewById(R.id.tv_remark);
        tv_remark.setText(content);

    }
    public interface ConfirmCallback{
        void confirm();
    }
}
