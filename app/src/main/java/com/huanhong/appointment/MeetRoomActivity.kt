package com.huanhong.appointment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.huanhong.appointment.adapter.CommonAdapter
import com.huanhong.appointment.adapter.ViewHolder
import com.huanhong.appointment.bean.Room
import com.huanhong.appointment.net.DialogUtils
import com.huanhong.appointment.net.Fault
import com.huanhong.appointment.net.httploader.BindMeetRoomsLoader
import com.huanhong.appointment.net.httploader.MeetRoomsLoader
import com.huanhong.appointment.net.httploader.UnbindMeetRoomsLoader
import com.huanhong.appointment.utils.SharedPreferencesUtils
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
    private var p = -1
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState )
        setContentView(R.layout.activity_meet_room)
        recycler_room.layoutManager = GridLayoutManager(this,4)
        recycler_room.adapter = object  : CommonAdapter<Room>(this, list,R.layout.item_room){
            override fun convert(holder: ViewHolder, t: MutableList<Room>) {
                val tv_name = holder.getView<TextView>(R.id.tv_name)
                val iv_select = holder.getView<View>(R.id.iv_select)
                val root = holder.getView<View>(R.id.root)

                tv_name.text = t[holder.realPosition].fullName
                if(t[holder.realPosition].deviceMac==null){
                    root.setBackgroundResource(R.drawable.shape_meet_unbind)
                }else{
                    root.setBackgroundResource(R.drawable.shape_meet_bind)
                }
                if(holder.realPosition==p){
                    iv_select.visibility= View.VISIBLE
                }else{
                    iv_select.visibility= View.GONE
                }

                root.setOnClickListener {
                    val temp = p
                     p  = holder.realPosition
                    if(temp>-1){
                        notifyItemChanged(temp)
                    }
                    notifyItemChanged(p)
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

        iv_confirm.setOnClickListener {
            confirm()
        }
        iv_back.setOnClickListener {
            onBackPressed()
        }

    }

    private fun confirm(){
        if(p>-1){
            if(list[p].deviceMac==null){
                ConfirmDialog(this@MeetRoomActivity,"是否确认绑定此会议室") {
                    bind(list[p])
                }.show()
            }else{
                ConfirmDialog(this@MeetRoomActivity,"当前会议室已有绑定门显设备，是否继续绑定") {
                    unbind(list[p])
                }.show()
            }
        }else{
            DialogUtils.ToastShow(this@MeetRoomActivity,"请选择会议室")
        }

    }

    @SuppressLint("CheckResult")
    private fun bind(room: Room){
        val deviceId = Settings.System.getString(contentResolver, Settings.System.ANDROID_ID)
        var homeDevice = HashMap<String,Any>()
        var map = HashMap<String,String>()
        map["roomId"] = room.id
        map["deviceMac"] = deviceId
        map["deviceName"] = room.fullName + " 会议室门显设备"
        homeDevice["homeDevice"] = map
        BindMeetRoomsLoader().bind(homeDevice).subscribe( {
            DialogUtils.ToastShow(this@MeetRoomActivity,"绑定成功")
            SharedPreferencesUtils.addData("roomName",room.fullName)
            SharedPreferencesUtils.addData("roomId",room.id)
            startActivity(Intent(this@MeetRoomActivity,MainActivity::class.java))
            finish()
        },{
            DialogUtils.ToastShow(this@MeetRoomActivity,"绑定失败")
        })
    }

    private fun unbind(room : Room){
        var map = java.util.HashMap<String, Any>()
        map["device"] = room.deviceMac
        UnbindMeetRoomsLoader().unbind(map).subscribe( {
            bind(room)
        },{
        })
    }
}