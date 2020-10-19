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
            String s = SharedPreferencesUtils.readData(flatsTag, "");
            if(s.equals( "7")){
                startActivity(new Intent(this,MainActivity.class));
            }else  if(s.equals( "8")){
                startActivity(new Intent(this,SeatActivity.class));
            }else  if(s.equals( "9")){
                startActivity(new Intent(this,EquipmentActivity.class));
            }else {
                startActivity(new Intent(this,LoginActivity.class));
            }
        }else {
            startActivity(new Intent(this,LoginActivity.class));
        }
        finish();
    }



    public static void setLogin(){
        // 做个缓存 已登录 和 flatsTag
        SharedPreferencesUtils.addData(login,true);
        SharedPreferencesUtils.addData(flatsTag, ""+flatsTag);
    }

    public static void clearLogin(){
        // 做个缓存 已登录 和 flatsTag
        SharedPreferencesUtils.addData(login,false);
        SharedPreferencesUtils.addData(flatsTag, "");
    }
}
