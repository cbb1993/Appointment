package com.huanhong.appointment.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.huanhong.appointment.NetworkChangeEvent
import com.huanhong.appointment.utils.NetWorkUtil
import org.greenrobot.eventbus.EventBus

/**
 * 网络状态接受广播，监听网络状态
 */
class NetworkChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val isConnected = NetWorkUtil.isNetworkConnected(context)
        EventBus.getDefault().post(NetworkChangeEvent(isConnected))
    }

}