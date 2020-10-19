package com.huanhong.appointment.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
public class SelectScreenPopwindow {

    public SelectScreenPopwindow(final Context context, View view, final ConfirmCallback confirmCallback){
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_screen,null);
        final PopupWindow  popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        popupWindow.setBackgroundDrawable(new ColorDrawable(0xc8000000));



        contentView.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        contentView.findViewById(R.id.tv_meeting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmCallback.confirm(7);
                popupWindow.dismiss();
            }
        });
        contentView.findViewById(R.id.tv_seat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmCallback.confirm(8);
                popupWindow.dismiss();
            }
        });

        contentView.findViewById(R.id.tv_equipment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmCallback.confirm(9);
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);
    }
    public interface ConfirmCallback{
        void confirm(int type);
    }
}
