package com.huanhong.appointment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.huanhong.appointment.bean.Meet
import com.huanhong.appointment.bean.Staff
import com.huanhong.appointment.net.DialogUtils
import com.huanhong.appointment.net.httploader.MeetAddLoader
import com.huanhong.appointment.net.httploader.MeetingUsersLoader
import com.huanhong.appointment.net.httploader.RoomMeetsLoader
import com.huanhong.appointment.utils.SharedPreferencesUtils
import kotlinx.android.synthetic.main.activity_order.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by 坎坎.
 * Date: 2019/7/29
 * Time: 9:20
 * describe:
 */
class OrderActivity:AppCompatActivity(){
    private var token:String?=null
    private var startStr =""
    private var endStr =""
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        token = intent.getStringExtra("token")
        getMeets()
        getPersons()

        tv_start.setOnClickListener {
            TimePopwindow(this@OrderActivity,tv_start){hour,minute ->
                var h = hour.toString()
                var m = minute.toString()
                if(hour<10){
                    h = "0$h"
                }
                if(minute<10){
                    m = "0$m"
                }
                startStr  = "$h:$m"
                if(validateStart()){
                    tv_start.text = startStr
                }else{
                    startStr  =""
                }
            }
        }
        tv_end.setOnClickListener {
            TimePopwindow(this@OrderActivity,tv_end){hour,minute ->
                var h = hour.toString()
                var m = minute.toString()
                if(hour<10){
                    h = "0$h"
                }
                if(minute<10){
                    m = "0$m"
                }
                endStr  = "$h:$m"
                if(validateEnd()){
                    tv_end.text = endStr
                }else{
                    endStr =""
                }
            }
        }

        btn_complete.setOnClickListener {
            add()
        }

    }

    private fun getCurrentDate():String{
       val  calendar =Calendar.getInstance()
        calendar.time = Date()
        var month = (calendar.get(Calendar.MONTH) +1).toString()
        if(month.length==1 ){
            month = "0$month"
        }
        var day = calendar.get(Calendar.DAY_OF_MONTH).toString()
        if(day.length==1 ){
            day = "0$day"
        }
       return "${calendar.get(Calendar.YEAR)}-$month-$day"
    }

    private val list = ArrayList<Meet>()
    @SuppressLint("CheckResult")
    private fun getMeets(){
        val deviceId = Settings.System.getString(contentResolver, Settings.System.ANDROID_ID)
        var map = HashMap<String,String>()
        map["device"] = deviceId
        RoomMeetsLoader().getRoomMeets(map).subscribe( { it ->
            list.clear()
            if(it.size > 0){
                it.forEach { info ->
                    if(info.gmtEnd.toLong() < getEndTime() ){
                        list.add(info)
                    }
                }
            }
            val timeList = ArrayList< RangeBar.TimeBean>()
            list.forEach {
                timeList.add(RangeBar.TimeBean(dateFormat(it.gmtStart),dateFormat(it.gmtEnd)))
            }
            range.setTimeRangeList(timeList)
        },{
            DialogUtils.ToastShow(this@OrderActivity,"请求出错")
        })
    }

    private fun validateStart():Boolean{
        var time = dateFormatToL(startStr)
        list.forEach {
            if(it.gmtStart.toLong()<= time && it.gmtEnd.toLong() >= time){
                DialogUtils.ToastShow(this@OrderActivity,"当前时间内有会议")
                return false
            }
        }
        if(time<=System.currentTimeMillis()){
            DialogUtils.ToastShow(this@OrderActivity,"开始时间不能小于当前时间")
            return false
        }
        if(endStr!=""){
            if(time>=dateFormatToL(endStr)){
                DialogUtils.ToastShow(this@OrderActivity,"开始时间不能大于结束时间")
                return false
            }
        }
        return true
    }

    private fun validateEnd():Boolean{
        var time = dateFormatToL(endStr)
        list.forEach {
            if(it.gmtStart.toLong()<= time && it.gmtEnd.toLong() >= time){
                DialogUtils.ToastShow(this@OrderActivity,"当前时间内有会议")
                return false
            }
        }
        if(time<=System.currentTimeMillis()){
            DialogUtils.ToastShow(this@OrderActivity,"结束时间不能小于当前时间")
            return false
        }
        if(startStr!=""){
            if(time<=dateFormatToL(startStr)){
                DialogUtils.ToastShow(this@OrderActivity,"结束时间不能小于开始时间")
                return false
            }
        }
        return true
    }


    @SuppressLint("CheckResult")
    private fun add(){
        if(et_name.length()==0){
            DialogUtils.ToastShow(this@OrderActivity,"请填写会议名称")
            return
        }
        if(startStr==""||endStr==""){
            DialogUtils.ToastShow(this@OrderActivity,"请选择开始时间和结束时间")
            return
        }
        if(joinUsers.isEmpty()){
            DialogUtils.ToastShow(this@OrderActivity,"请选择参会人员")
            return
        }
        val map = HashMap<String,Any>()
        map["name"] = et_name.text.toString()
        map["gmtStart"] =  getCurrentDate() +" "+tv_start.text.toString() + ":00"
        map["gmtEnd"] =  getCurrentDate() +" "+tv_end.text.toString() + ":00"
        map["roomId"] = SharedPreferencesUtils.readData("roomId")
        map["roomName"] = SharedPreferencesUtils.readData("roomName")
        map["meetingUsers"] = joinUsers
        MeetAddLoader().request(token,map).subscribe({
            DialogUtils.ToastShow(this@OrderActivity,"添加成功")
            startActivity(Intent(this@OrderActivity,MainActivity::class.java))
        },{})
    }
    data class JoinUser(var attendeeId:String,var attendeeName:String)

    // 获得可参会人员
    private val userList = ArrayList<Staff.User>()
    @SuppressLint("CheckResult")
    private fun getPersons(){
        MeetingUsersLoader().getMeetingUsers("Bearer_$token").subscribe( { it ->
            userList.clear()
            it.forEach { staff ->
                if(staff.users!=null){
                    staff.users.forEach { user->

                        var exsit = false
                        userList.forEach {  u ->
                            if(u.userId == user.userId  ){
                                exsit = true
                            }
                        }
                        if(!exsit){
                            userList.add(user)
                        }
                    }
                }
            }
        },{
            DialogUtils.ToastShow(this@OrderActivity,"请求出错")
        })


        tv_user.setOnClickListener {
            UsersPopwindow(this@OrderActivity,tv_user,userList, UsersPopwindow.ConfirmCallback { it ->
                joinUsers.clear()
                val buffer = StringBuffer()
                it.forEach {
                    joinUsers.add(JoinUser(it.userId,it.userName))
                    if(buffer.isEmpty()){
                        buffer.append(it.userName)
                    }else{
                        buffer.append(",").append(it.userName)
                    }
                    tv_user.text = buffer.toString()
                }


            })
        }
    }
    private val joinUsers = ArrayList<JoinUser>()

    private fun getEndTime() :Long{
        val todayEnd = Calendar.getInstance ()
        todayEnd.set(Calendar.HOUR_OF_DAY, 23)
        todayEnd.set(Calendar.MINUTE, 59)
        todayEnd.set(Calendar.SECOND, 59)
        todayEnd.set(Calendar.MILLISECOND, 999)
        return todayEnd.timeInMillis
    }
    var format : SimpleDateFormat?=null


    private fun dateFormatToL(time:String):Long{
        val t = getCurrentDate() +" "+time + ":00"
        if(format==null){
            format =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        }
        val date =format!!.parse(t)
        return date.time
    }

    private fun dateFormat(time:String):String{
        var timeL = time.toLong()
        return dateFormat(timeL)
    }
    private fun dateFormat(time:Long):String{
        if(format==null){
            format =  SimpleDateFormat("HH:mm")
        }
        return format!!.format(Date(time))
    }
}