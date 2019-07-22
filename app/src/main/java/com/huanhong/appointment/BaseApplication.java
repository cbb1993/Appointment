package com.huanhong.appointment;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;

public class BaseApplication extends Application {
    public static Application application;

    public static Context instance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        initPushService(this);
    }

    /**
     * 初始化云推送通道
     * @param applicationContext
     */private void initPushService(final Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.e("---------","success");
            }
            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e("---------","-------"+errorCode);
                Log.e("---------","-------"+errorMessage);
            }
        });
    }
}
