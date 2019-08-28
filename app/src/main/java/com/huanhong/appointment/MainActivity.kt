package com.huanhong.appointment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory
import com.huanhong.appointment.bean.Meet
import com.huanhong.appointment.net.DialogUtils
import com.huanhong.appointment.net.httploader.RoomMeetsLoader
import com.huanhong.appointment.net.httploader.UnbindMeetRoomsLoader
import com.huanhong.appointment.utils.SharedPreferencesUtils
import com.smbd.peripheral.SmbdLed
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by 坎坎.
 * Date: 2019/7/10
 * Time: 13:29
 * describe:
 */
class MainActivity: AppCompatActivity(){


    lateinit var timeHandler :Handler
    lateinit var calendar: Calendar
//    lateinit var mSmbdLed : SmbdLed

    private var start = 0
    private var end = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)
//        mSmbdLed = SmbdLed()
//        mSmbdLed.onAll(false)
        setMeetData()
        initTimer()
        tv_lang.setOnClickListener {
            StringConstant.isChinses = !StringConstant.isChinses
            setMeetData()
        }


        iv_setting.setOnClickListener {
            ConfirmDialog(this@MainActivity,"是否确认解绑此会议室") {
                unbind()
            }.show()
        }
        bindPush()

        room_name.text = SharedPreferencesUtils.readData("roomName")!!


        tv_order.setOnClickListener {
            startActivity(Intent(this@MainActivity,OrderLoginActivity::class.java))
        }
    }

    private fun validateTime(){
    }

    override fun onResume() {
        super.onResume()
        getMeets()
    }

    private fun initTimer(){
        calendar = Calendar.getInstance()
        calendar.time = Date()

        var hour = calendar.get(Calendar.HOUR_OF_DAY).toString()
        if(hour.length==1 ){
            hour = "0$hour"
        }
        var minute = calendar.get(Calendar.MINUTE).toString()
        if(minute.length==1 ){
            minute = "0$minute"
        }


        range.setCurrentTime("$hour:$minute")

        timeHandler = @SuppressLint("HandlerLeak")
        object :Handler(){
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                setTimer()
                sendEmptyMessageDelayed(1,1000)
            }
        }
        timeHandler.sendEmptyMessage(1)
    }
    private fun setTimer(){
        calendar.time = Date()
        var month = (calendar.get(Calendar.MONTH) +1).toString()
        if(month.length==1 ){
            month = "0$month"
        }
        var day = calendar.get(Calendar.DAY_OF_MONTH).toString()
        if(day.length==1 ){
            day = "0$day"
        }
        var hour = calendar.get(Calendar.HOUR_OF_DAY).toString()
        if(hour.length==1 ){
            hour = "0$hour"
        }
        var minute = calendar.get(Calendar.MINUTE).toString()
        if(minute.length==1 ){
            minute = "0$minute"
        }

        var second = calendar.get(Calendar.SECOND).toString()
        if(second.length ==1 ){
            second = "0$second"
        }

        val date = "${calendar.get(Calendar.YEAR)}-$month-$day $hour:$minute:$second"
        tv_date.text = date

        if(calendar.get(Calendar.SECOND) == 0){
            range.setCurrentTime("$hour:$minute")
        }
        if(list.size>0){
            var meet = list[0]
            if(meet.gmtEnd<System.currentTimeMillis()){
                if(list.size>1){
                    meet = list[1]
                }else{
                    return
                }
            }
            if(System.currentTimeMillis() >=meet.gmtStart && System.currentTimeMillis() <= meet.gmtEnd){
                if(!use && !load ){
                    getMeets()
                }
            }else {
                if(use && !load){
                    getMeets()
                }
            }
        }

    }

    private var use = false
    private var load = false

    private fun setMeetData(){
        if(StringConstant.isChinses){
            tv_lang.text = StringConstant.language_cn
        }else{
            tv_lang.text = StringConstant.language_en
        }
        if(list.size>0){
            var meet = list[0]
            if(meet.gmtEnd<System.currentTimeMillis()){
                if(list.size > 1){
                     meet = list[1]
                }else{
                    use = false
                    // 空闲
                    view_line.setBackgroundColor(resources.getColor(R.color.green))
//                  mSmbdLed.onGreen(true)
                    tv_time.visibility=View.GONE
                    ll_creator.visibility=View.GONE
                    ll_count.visibility=View.GONE
                    tv_title.text = "空闲"
                    if(StringConstant.isChinses){
                        tv_next.text = StringConstant.next_conference_cn + StringConstant.none_cn
                    }else{
                        tv_next.text = StringConstant.next_conference_en + StringConstant.none_en
                    }
                    return
                }
            }
            if(System.currentTimeMillis() < meet.gmtStart){
                use = false
               // 空闲
                view_line.setBackgroundColor(resources.getColor(R.color.green))
//                mSmbdLed.onGreen(true)
                tv_time.visibility=View.GONE
                ll_creator.visibility=View.GONE
                ll_count.visibility=View.GONE
                tv_title.text = "空闲"
                if(StringConstant.isChinses){
                    tv_next.text = StringConstant.next_conference_cn + meet.name
                }else{
                    tv_next.text = StringConstant.next_conference_en + meet.name
                }
            }else if(System.currentTimeMillis() < meet.gmtEnd){
//                mSmbdLed.onRed(true)
                use = true
                view_line.setBackgroundColor(resources.getColor(R.color.red))
                tv_time.visibility=View.VISIBLE
                ll_creator.visibility=View.VISIBLE
                ll_count.visibility=View.VISIBLE
                tv_time.text = dateFormat(meet.gmtStart) +"-" +dateFormat(meet.gmtEnd)
                tv_creator_name.text = meet.creatorName
                tv_count_num.text = meet.peopleNum
                if(StringConstant.isChinses){
                    tv_creator.text = StringConstant.creator_cn
                }else{
                    tv_creator.text = StringConstant.creator_en
                }
                tv_title.text = meet.name
                if(list.size > 1){
                    if(StringConstant.isChinses){
                        tv_next.text = StringConstant.next_conference_cn + list[1].name
                    }else{
                        tv_next.text = StringConstant.next_conference_en + list[1].name
                    }
                }else{
                    if(StringConstant.isChinses){
                        tv_next.text = StringConstant.next_conference_cn + StringConstant.none_cn
                    }else{
                        tv_next.text = StringConstant.next_conference_en + StringConstant.none_en
                    }
                }
            }
        }else{
            use = false
            // 空闲
            view_line.setBackgroundColor(resources.getColor(R.color.green))
//            mSmbdLed.onGreen(true)
            tv_time.visibility=View.GONE
            ll_creator.visibility=View.GONE
            ll_count.visibility=View.GONE
            tv_title.text = "空闲"
            if(StringConstant.isChinses){
                tv_next.text = StringConstant.next_conference_cn + StringConstant.none_cn
            }else{
                tv_next.text = StringConstant.next_conference_en + StringConstant.none_en
            }
        }
    }


    private val list = ArrayList<Meet>()
    @SuppressLint("CheckResult")
    private fun getMeets(){
        load = true
        val deviceId = Settings.System.getString(contentResolver, Settings.System.ANDROID_ID)
        var map = HashMap<String,String>()
        map["device"] = deviceId
        RoomMeetsLoader().getRoomMeets(map).subscribe( { it ->
            list.clear()
            if(it.size > 0){
                it.forEach { info ->
                    if(info.gmtEnd < getEndTime() ){
                        list.add(info)
                    }
                }
            }
            val timeList = ArrayList< RangeBar.TimeBean>()
            list.forEach {
                timeList.add(RangeBar.TimeBean(dateFormat(it.gmtStart),dateFormat(it.gmtEnd)))
            }
            range.setTimeRangeList(timeList)
            load = false
            setMeetData()
        },{
            load = false
            DialogUtils.ToastShow(this@MainActivity,"请求出错")
        })
    }

    private fun unbind(){
        val deviceId = Settings.System.getString(contentResolver, Settings.System.ANDROID_ID)
        var map = HashMap<String,Any>()
        map["device"] = deviceId
        UnbindMeetRoomsLoader().unbind(map).subscribe( {
            DialogUtils.ToastShow(this@MainActivity,"解绑成功")
            unbindPush()
            startActivity(Intent(this@MainActivity,LoginActivity::class.java))
        },{
            DialogUtils.ToastShow(this@MainActivity,"解绑失败")
        })
    }

    var format :SimpleDateFormat?=null
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

//    private fun dateFormatToL(time:String):Long{
//        var timeL = time.toLong()
//        return dateFormatToL(timeL)
//    }
//    private fun dateFormatToL(time:Long):Long{
//        var timeStr = dateFormat(time)
//        if(format==null){
//            format =  SimpleDateFormat("HH:mm")
//        }
//        val date =format!!.parse(timeStr)
//        return date.time
//    }


    private fun getEndTime() :Long{
        val todayEnd = Calendar.getInstance ()
        todayEnd.set(Calendar.HOUR_OF_DAY, 23)
        todayEnd.set(Calendar.MINUTE, 59)
        todayEnd.set(Calendar.SECOND, 59)
        todayEnd.set(Calendar.MILLISECOND, 999)
        return todayEnd.timeInMillis
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun notifyChange(str:String){
        if(str == "100"){
            getMeets()
        }else if(str == "200"){
            AlertDialog(this@MainActivity,"会议室已和本机解绑"){
                startActivity(Intent(this@MainActivity,LoginActivity::class.java))
            }.show()
        }

    }


    private fun bindPush(){
        PushServiceFactory.getCloudPushService().bindAccount(Settings.System.getString(contentResolver, Settings.System.ANDROID_ID), object : CommonCallback {
            override fun onSuccess(p0: String?) {
                Log.e("---push------", "success")
            }
            override fun onFailed(p0: String?, p1: String?) {
                Log.e("---push------", "---"+p0)
                Log.e("---push------", "---"+p1)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindPush()
        EventBus.getDefault().unregister(this)
    }

    private fun unbindPush(){
        PushServiceFactory.getCloudPushService().unbindAccount(object : CommonCallback {
            override fun onSuccess(p0: String?) {
            }
            override fun onFailed(p0: String?, p1: String?) {
            }
        })
    }
}