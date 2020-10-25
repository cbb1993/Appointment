package com.huanhong.appointment.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import com.huanhong.appointment.utils.SharedPreferencesUtils;

import org.jetbrains.annotations.Nullable;

public class SplashActivity extends BaseActivity {
    public static final String login = "login";
    public static final String flatsTag = "flatsTag";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 判断是否已经登录
        boolean b = SharedPreferencesUtils.readBooleanData(login, false);
        if(b){
            int  f= SharedPreferencesUtils.readIntData(flatsTag);
            switch (f){
                case 7:
                    startActivity(new Intent(this,MainActivity.class));
                    break;
                case 8:
                    startActivity(new Intent(this,SeatActivity.class));
                    break;
                case 9:
                    startActivity(new Intent(this,EquipmentActivity.class));
                    break;
                default:
                    startActivity(new Intent(this,LoginActivity.class));
                    break;
            }
        }else {
            startActivity(new Intent(this,LoginActivity.class));
        }
        finish();
    }



    public static void setLogin(int f){
        // 做个缓存 已登录 和 flatsTag
        SharedPreferencesUtils.addData(login,true);
        SharedPreferencesUtils.addData(flatsTag, f);
    }

    public static void clearLogin(){
        SharedPreferencesUtils.addData(login,false);
        SharedPreferencesUtils.addData(flatsTag, 0);
    }
}
