package com.huanhong.appointment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huanhong.appointment.bean.Meet;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 坎坎.
 * Date: 2019/9/4
 * Time: 15:53
 * describe:
 */
public class MeetPop {


    public MeetPop(Context context, Meet meet) {
        initPop(context,meet);
    }

    private PopupWindow popupWindow;

    private void initPop(Context context, Meet meet) {

        View view = LayoutInflater.from(context).inflate(R.layout.pop_meet, null);

        if (popupWindow == null) {
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        }
        // 设置PopupWindow的背景
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置PopupWindow是否能响应外部点击事件
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        TextView tv_title = view.findViewById(R.id.tv_title);
        TextView tv_time = view.findViewById(R.id.tv_time);
        TextView tv_creator = view.findViewById(R.id.tv_creator);
        TextView tv_delay = view.findViewById(R.id.tv_delay);

        tv_title.setText(meet.name);
        tv_time.setText(dateFormat(meet.gmtStart) + "-" + dateFormat(meet.gmtEnd));
        tv_creator.setText(meet.creatorName);
        String s = meet.delayTimeStr;
        if("".equals(s)){
            tv_delay.setText("0分钟");
        }else {
            tv_delay.setText(s+"分钟");
        }


    }

    public void show(View view){
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        popupWindow.showAsDropDown(view,-120,0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(popupWindow!=null){
                    popupWindow.dismiss();
                }
            }
        },5000);
    }

    private String dateFormat(long l) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(l));
    }
}
