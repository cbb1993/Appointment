package com.huanhong.appointment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.huanhong.appointment.adapter.CommonAdapter
import com.huanhong.appointment.adapter.ViewHolder
import com.huanhong.appointment.bean.Room
import com.huanhong.appointment.net.DialogUtils
import com.huanhong.appointment.net.Fault
import com.huanhong.appointment.net.httploader.BindMeetRoomsLoader
import com.huanhong.appointment.net.httploader.MeetRoomsLoader
import com.huanhong.appointment.net.httploader.UnbindMeetRoomsLoader
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_meet_room.*

/**
 * Created by 坎坎.
 * Date: 2019/7/19
 * Time: 11:19
 * describe:
 */
class MeetRoomActivity:AppCompatActivity(){
    private val list = ArrayList<Room>()
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState )
        setContentView(R.layout.activity_meet_room)
        recycler_room.layoutManager = LinearLayoutManager(this)
        recycler_room.adapter = object  : CommonAdapter<Room>(this, list,R.layout.item_room){
            override fun convert(holder: ViewHolder, t: MutableList<Room>) {
                val tv_name = holder.getView<TextView>(R.id.tv_name)
                val bind = holder.getView<TextView>(R.id.bind)
                tv_name.text = t[holder.realPosition].fullName
                if(t[holder.realPosition].deviceMac==null){
                    bind.visibility = View.GONE
                }else{
                    bind.visibility = View.VISIBLE
                }

                holder.getView<View>(R.id.root).setOnClickListener {
                    if(t[holder.realPosition].deviceMac==null){
                        ConfirmDialog(this@MeetRoomActivity,"是否确认绑定此会议室") {
                            bind(t[holder.realPosition])
                        }.show()
                    }else{
                        ConfirmDialog(this@MeetRoomActivity,"当前会议室已有绑定门显设备，是否继续绑定") {
                            unbind(t[holder.realPosition])
                        }.show()
                    }

                }
            }
        }

        MeetRoomsLoader().rooms.subscribe({
            if(it!=null){
                list.addAll(it)
                recycler_room.adapter!!.notifyDataSetChanged()
            }
        },{
        })
    }

    @SuppressLint("CheckResult")
    private fun bind(room: Room){
        val deviceId = Settings.System.getString(contentResolver, Settings.System.ANDROID_ID)
        var homeDevice = HashMap<String,Any>()
        var map = HashMap<String,String>()
        map["roomId"] = room.id
        map["deviceMac"] = deviceId
        map["deviceName"] = room.fullName + " " +deviceId
        homeDevice["homeDevice"] = map
        BindMeetRoomsLoader().bind(homeDevice).subscribe( {
            DialogUtils.ToastShow(this@MeetRoomActivity,"绑定成功")
            startActivity(Intent(this@MeetRoomActivity,MainActivity::class.java))
            finish()
        },{
            DialogUtils.ToastShow(this@MeetRoomActivity,"绑定失败")
        })
    }

    private fun unbind(room : Room){
        val deviceId = Settings.System.getString(contentResolver, Settings.System.ANDROID_ID)
        var map = java.util.HashMap<String, Any>()
        map["device"] = deviceId
        UnbindMeetRoomsLoader().unbind(map).subscribe( {
            bind(room)
        },{
        })
    }
}