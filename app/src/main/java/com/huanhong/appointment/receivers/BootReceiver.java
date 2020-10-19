package com.huanhong.appointment.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.huanhong.appointment.activitys.SplashActivity;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("Broadcast->", "onReceive: " + action);
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            startOneActivity(context);
        }
    }

    private void startOneActivity(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
