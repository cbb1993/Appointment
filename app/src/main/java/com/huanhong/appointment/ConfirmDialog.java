package com.huanhong.appointment;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by 坎坎.
 * Date: 2019/6/6
 * Time: 11:32
 * describe:
 */
public class ConfirmDialog extends Dialog {
    public ConfirmDialog(Context context,String content, ConfirmCallback callback) {
        super(context,R.style.app_dialog);
        init(content,callback);
    }
    private TextView tv_content;
    private void init(String content, final ConfirmCallback callback) {
        setContentView(R.layout.dialog_confirm);
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
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tv_content = findViewById(R.id.tv_content);
        tv_content.setText(content);
    }
    public interface ConfirmCallback{
        void confirm();
    }
}
