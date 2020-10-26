package com.huanhong.appointment.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;


import com.huanhong.appointment.R;
import com.huanhong.appointment.utils.SharedPreferencesUtils;

import org.jetbrains.annotations.Nullable;

public class SplashActivity extends BaseActivity {
    public static final String login = "login";
    public static final String flatsTag = "flatsTag";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tv_msg = findViewById(R.id.tv_msg);
        final boolean b = SharedPreferencesUtils.readBooleanData(login, false);
        if(b){
            int  f= SharedPreferencesUtils.readIntData(flatsTag);
            switch (f){
                case 7:
                    tv_msg.setText("准备进入会议室...请稍等");
                    break;
                case 8:
                    tv_msg.setText("准备进入工位区...请稍等");
                    break;
                case 9:
                    tv_msg.setText("准备进入健身房...请稍等");
                    break;
                default:
                    tv_msg.setText("准备进入登录页...请稍等");
                    break;
            }
        }else {
            tv_msg.setText("准备进入登录页...请稍等");
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(b){
                    int  f= SharedPreferencesUtils.readIntData(flatsTag);
                    switch (f){
                        case 7:
                            startActivity(new Intent(SplashActivity.this,MainActivity.class));
                            break;
                        case 8:
                            startActivity(new Intent(SplashActivity.this,SeatActivity.class));
                            break;
                        case 9:
                            startActivity(new Intent(SplashActivity.this,EquipmentActivity.class));
                            break;
                        default:
                            startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                            break;
                    }
                }else {
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                }
                finish();
            }
        },2000);


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
