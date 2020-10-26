package com.huanhong.appointment.activitys

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.TextView
import com.huanhong.appointment.views.ConfirmDialog
import com.huanhong.appointment.R
import com.huanhong.appointment.views.SelectScreenPopwindow
import com.huanhong.appointment.adapter.CommonAdapter
import com.huanhong.appointment.adapter.ViewHolder
import com.huanhong.appointment.bean.Room
import com.huanhong.appointment.net.DialogUtils
import com.huanhong.appointment.net.httploader.BindMeetRoomsLoader
import com.huanhong.appointment.net.httploader.MeetRoomsLoader
import com.huanhong.appointment.net.httploader.UnbindMeetRoomsLoader
import com.huanhong.appointment.utils.SharedPreferencesUtils
import com.huanhong.appointment.views.ConfigPopwindow
import kotlinx.android.synthetic.main.activity_meet_room.*

/**
 * Created by 坎坎.
 * Date: 2019/7/19
 * Time: 11:19
 * describe:
 */
class MeetRoomActivity : BaseActivity() {
    private val list = ArrayList<Room>()
    private var p = -1

    //    private var flatsTag: Int = 7
    private var areaType: Int = 0

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meet_room)
        recycler_room.layoutManager = GridLayoutManager(this, 4)
        recycler_room.adapter = object : CommonAdapter<Room>(this, list, R.layout.item_room) {
            override fun convert(holder: ViewHolder, t: MutableList<Room>) {
                val tv_name = holder.getView<TextView>(R.id.tv_name)
                val iv_select = holder.getView<View>(R.id.iv_select)
                val root = holder.getView<View>(R.id.root)

                tv_name.text = t[holder.realPosition].roomName
                if (t[holder.realPosition].deviceMac == null) {
                    root.setBackgroundResource(R.drawable.shape_meet_unbind)
                } else {
                    root.setBackgroundResource(R.drawable.shape_meet_bind)
                }
                if (holder.realPosition == p) {
                    iv_select.visibility = View.VISIBLE
                } else {
                    iv_select.visibility = View.GONE
                }

                root.setOnClickListener {
                    val temp = p
                    p = holder.realPosition
                    if (temp > -1) {
                        notifyItemChanged(temp)
                    }
                    notifyItemChanged(p)
                }
            }
        }


        iv_confirm.setOnClickListener {
            confirm()
        }
        iv_back.setOnClickListener {
            onBackPressed()
        }

//        iv_setting.setOnClickListener {
//            SelectScreenPopwindow(this, iv_back, SelectScreenPopwindow.ConfirmCallback {
//                flatsTag = it
//                when (it) {
//                    7 -> tv_title.text = "绑定会议室"
//                    8 -> tv_title.text = "绑定办公区"
//                    9 -> tv_title.text = "绑定健身房"
//                }
//                request(it)
//            })
//        }

        // 获得需要显示的view
        areaType = ConfigPopwindow.getAreaType()
        when (areaType) {
            0 -> tv_title.text = "绑定会议室"
            1 -> tv_title.text = "绑定办公区"
            2 -> tv_title.text = "绑定健身房"
        }
        request(areaType + 7)
    }

    @SuppressLint("CheckResult")
    private fun request(flatsTag: Int) {
        // 重置选择
        p = -1
        val map = HashMap<String, String>()
        map["flatsTag"] = "" + flatsTag
        MeetRoomsLoader().getRooms(map).subscribe({
            list.clear()
            if (it != null) {
                list.addAll(it)
            }
            recycler_room.adapter!!.notifyDataSetChanged()

        }, {
        })
    }

    private fun confirm() {
        if (p > -1) {
            if (list[p].deviceMac == null) {
                ConfirmDialog(this@MeetRoomActivity, "是否确认绑定") {
                    bind(list[p])
                }.show()
            } else {
                ConfirmDialog(this@MeetRoomActivity, "当前区域已有绑定门显设备，是否继续绑定") {
                    unbind(list[p])
                }.show()
            }
        } else {
            DialogUtils.ToastShow(this@MeetRoomActivity, "请选择绑定区域")
        }

    }

    @SuppressLint("CheckResult")
    private fun bind(room: Room) {
        val deviceId = Settings.System.getString(contentResolver, Settings.System.ANDROID_ID)
        var homeDevice = HashMap<String, Any>()
        var map = HashMap<String, String>()
        map["roomId"] = room.id
        map["deviceMac"] = deviceId
        map["deviceName"] = room.fullName + " 会议室门显设备"
        homeDevice["homeDevice"] = map
        BindMeetRoomsLoader().bind(homeDevice).subscribe({
            DialogUtils.ToastShow(this@MeetRoomActivity, "绑定成功")
            SharedPreferencesUtils.addData("roomName", room.roomName)
            SharedPreferencesUtils.addData("roomId", room.id)
            SplashActivity.setLogin(areaType + 7)
            when (areaType) {
                0 -> startActivity(Intent(this@MeetRoomActivity, MainActivity::class.java))
                1 -> startActivity(Intent(this@MeetRoomActivity, SeatActivity::class.java))
                2 -> startActivity(Intent(this@MeetRoomActivity, EquipmentActivity::class.java))
            }
            finish()
        }, {
            DialogUtils.ToastShow(this@MeetRoomActivity, "绑定失败")
        })
    }

    private fun unbind(room: Room) {
        var map = java.util.HashMap<String, Any>()
        map["device"] = room.deviceMac
        UnbindMeetRoomsLoader().unbind(map).subscribe({
            bind(room)
        }, {
        })
    }
}