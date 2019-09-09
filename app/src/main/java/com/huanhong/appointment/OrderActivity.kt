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
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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
        EventBus.getDefault().register(this)
        setContentView(R.layout.activity_order)
        token = intent.getStringExtra("token")
        getMeets()
        getPersons()

        iv_back.setOnClickListener {
            startActivity(Intent(this@OrderActivity,MainActivity::class.java))
        }

        tv_start_click.setOnClickListener {
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
                    setTime()
                }else{
                    startStr  =""
                }
            }
        }
        tv_end_click.setOnClickListener {
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
                    setTime()
                }else{
                    endStr =""
                }
            }
        }

        rg_select_sign.check(R.id.rb_sign_1)
        rg_select_notify.check(R.id.rb_notify_1)

        btn_complete.setOnClickListener {
            add()
        }

    }

    private fun setTime(){
        if(startStr!=""&&endStr!=""){
            range.setTimeRange(RangeBar.TimeBean(startStr,endStr,0),100,1)
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
            if(it.list.size > 0){
                it.list.forEach { info ->
                    if(info.gmtEnd < getEndTime() && info.gmtStart > getStartTime()){
                        list.add(info)
                    }
                }
            }
            val timeList = ArrayList<RangeBar.TimeBean>()
            list.forEach {
                // 会议结束时间是否在当前时间之前  会议是结束了
                if (System.currentTimeMillis() >= it.gmtEnd || it.state ==2) {
                    timeList.add(RangeBar.TimeBean(dateFormat(it.gmtStart), dateFormat(it.gmtEnd), 2))
                } else {
                    timeList.add(RangeBar.TimeBean(dateFormat(it.gmtStart), dateFormat(it.gmtEnd), 1))
                }
            }
            range.setTimeRangeList(timeList)
        },{
            DialogUtils.ToastShow(this@OrderActivity,"请求出错")
        })
    }

    private var signInType = 0
    private var notificationType = 1
    private fun getSelect(){
        when(rg_select_sign.checkedRadioButtonId){
            R.id.rb_sign_1 -> signInType = 0
            R.id.rb_sign_2 -> signInType = 1
            R.id.rb_sign_3 -> signInType = 2
            R.id.rb_sign_4 -> signInType = 3
        }
        when(rg_select_notify.checkedRadioButtonId){
            R.id.rb_notify_1 -> notificationType = 1
            R.id.rb_notify_2 -> notificationType = 2
            R.id.rb_notify_3 -> notificationType = 3
            R.id.rb_notify_4 -> notificationType = 4
            R.id.rb_notify_5 -> notificationType = 5
            R.id.rb_notify_6 -> notificationType = 6
            R.id.rb_notify_7 -> notificationType = 7

        }
    }

    private fun validateStart():Boolean{
        var time = dateFormatToL(startStr)
        list.forEach {
            if(it.gmtStart<= time && it.gmtEnd>= time){
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
            if(it.gmtStart<= time && it.gmtEnd>= time){
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
        if(!validateStart()){
            return
        }
        getSelect()

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
        map["signInType"] =signInType
        map["notificationType"] = notificationType
        map["meetingUsers"] = joinUsers
        MeetAddLoader().request(token,map).subscribe({
            if(MainActivity.needAudit == 1){
                SuccessDialog(this@OrderActivity,"该会议需要通过审核，请您耐心等待"){
                    startActivity(Intent(this@OrderActivity,MainActivity::class.java))
                }.show()
            }else{
                SuccessDialog(this@OrderActivity,""){
                    startActivity(Intent(this@OrderActivity,MainActivity::class.java))
                }.show()
            }

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


        tv_user_click.setOnClickListener {
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
    private fun getStartTime(): Long {
        val todayS = Calendar.getInstance()
        todayS.set(Calendar.HOUR_OF_DAY, 0)
        todayS.set(Calendar.MINUTE, 0)
        todayS.set(Calendar.SECOND, 0)
        todayS.set(Calendar.MILLISECOND, 0)
        return todayS.timeInMillis
    }
    private fun getEndTime() :Long{
        val todayEnd = Calendar.getInstance ()
        todayEnd.set(Calendar.HOUR_OF_DAY, 23)
        todayEnd.set(Calendar.MINUTE, 59)
        todayEnd.set(Calendar.SECOND, 59)
        todayEnd.set(Calendar.MILLISECOND, 999)
        return todayEnd.timeInMillis
    }



    private fun dateFormatToL(time:String):Long{
        val t = getCurrentDate() +" "+time + ":00"
        val format =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date =format!!.parse(t)
        return date.time
    }

    private fun dateFormat(time:String):String{
        var timeL = time.toLong()
        return dateFormat(timeL)
    }
    private fun dateFormat(time:Long):String{
        val format =  SimpleDateFormat("HH:mm")
        return format!!.format(Date(time))
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun notifyChange(str: String) {
        if (str == "100") {
            getMeets()
        } else if (str == "200") {
            AlertDialog(this@OrderActivity, "会议室已和本机解绑") {
                startActivity(Intent(this@OrderActivity, LoginActivity::class.java))
            }.show()
        }

    }

}