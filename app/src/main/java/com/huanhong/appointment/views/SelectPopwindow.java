package com.huanhong.appointment.views;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TimePicker;

import com.huanhong.appointment.R;

/**
 * Created by 坎坎.
 * Date: 2019/7/2
 * Time: 15:46
 * describe:
 */
public class SelectPopwindow {
    public SelectPopwindow(Context context, View view, final ConfirmCallback confirmCallback){
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_number,null);
        final PopupWindow  popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        popupWindow.setBackgroundDrawable(new ColorDrawable(0xc8000000));
        popupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);

        View tv_cancel =contentView.findViewById(R.id.tv_cancel);
        View tv_confirm =contentView.findViewById(R.id.tv_confirm);

        final NumberPicker time_picker =contentView.findViewById(R.id.number_picker);

        final String [] s = {"10","20","30","40","50","60"};
        String [] ss = {"10分钟","20分钟","30分钟","40分钟","50分钟","60分钟"};
        time_picker.setMaxValue(5);
        time_picker.setMinValue(0);
        time_picker.setDisplayedValues(ss);
        time_picker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confirmCallback!=null){
                    popupWindow.dismiss();
                    confirmCallback.confirm(s[time_picker.getValue()]);
                }
            }
        });
    }
    public interface ConfirmCallback{
        void confirm(String s);
    }
}
