package com.huanhong.appointment.views;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TimePicker;

import com.huanhong.appointment.R;
import com.huanhong.appointment.bean.Staff;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 坎坎.
 * Date: 2019/7/2
 * Time: 15:46
 * describe:
 */
public class TimePopwindow {
    public TimePopwindow(Context context, View view, final ConfirmCallback confirmCallback){
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_time,null);
        final PopupWindow  popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        popupWindow.setBackgroundDrawable(new ColorDrawable(0xc8000000));
        popupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);

        View tv_cancel =contentView.findViewById(R.id.tv_cancel);
        View tv_confirm =contentView.findViewById(R.id.tv_confirm);
        final TimePicker time_picker =contentView.findViewById(R.id.time_picker);

        time_picker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

        time_picker.setIs24HourView(true);

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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        confirmCallback.confirm(time_picker.getHour(),time_picker.getMinute());
                        popupWindow.dismiss();
                    }
                }
            }
        });
    }
    public interface ConfirmCallback{
        void confirm(int hourOfDay, int minute);
    }
}
