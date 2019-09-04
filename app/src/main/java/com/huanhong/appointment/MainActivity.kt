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
import com.huanhong.appointment.net.httploader.DelayMeetMeetRoomsLoader
import com.huanhong.appointment.net.httploader.EndMeetMeetRoomsLoader
import com.huanhong.appointment.net.httploader.RoomMeetsLoader
import com.huanhong.appointment.net.httploader.UnbindMeetRoomsLoader
import com.huanhong.appointment.utils.SharedPreferencesUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by 坎坎.
 * Date: 2019/7/10
 * Time: 13:29
 * describe:
 */
class MainActivity : AppCompatActivity() {


    lateinit var timeHandler: Handler
    lateinit var calendar: Calendar

    private var roomName = ""
    private var currentMeet : Meet? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)
        setMeetData()
        initTimer()

        iv_setting.setOnClickListener {
            ConfirmDialog(this@MainActivity, "是否确认解绑此会议室") {
                unbind()
            }.show()
        }
        bindPush()
        roomName = SharedPreferencesUtils.readData("roomName")!!

        tv_order.setOnClickListener {
            startActivity(Intent(this@MainActivity, OrderLoginActivity::class.java))
        }

        tv_end.setOnClickListener {
            if(currentMeet !=null){
                val  map = HashMap<String,Any?>()
                map["id"] = currentMeet?.id
                EndMeetMeetRoomsLoader().requset(map).subscribe({
                    DialogUtils.ToastShow(this@MainActivity, "会议已经结束")
                    getMeets()
                }, {
                    DialogUtils.ToastShow(this@MainActivity, "结束失败")
                })
            }
        }

        tv_delay.setOnClickListener {
            if(ll_delay.isShown){
                ll_delay.visibility=View.GONE
            }else{
                ll_delay.visibility=View.VISIBLE
            }
        }

        tv_delay_15.setOnClickListener {
            delay("15")
        }
        tv_delay_30.setOnClickListener {
            delay("30")
        }
        tv_delay_60.setOnClickListener {
            delay("60")
        }

        range.setMeetClickListner {it ->
            if(it<httpList.size){
                MeetPop(this@MainActivity,httpList[it]).show(pop_line)
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun delay(time:String){
        val  map = HashMap<String,Any?>()
        map["id"] = currentMeet?.id
        map["delayTimeStr"] = time
        DelayMeetMeetRoomsLoader().requset(map).subscribe({
            DialogUtils.ToastShow(this@MainActivity, "延时成功")
            ll_delay.visibility=View.GONE
            getMeets()
        }, {
            DialogUtils.ToastShow(this@MainActivity, "延时失败")
        })
    }

    override fun onResume() {
        super.onResume()
        getMeets()
    }

    private fun initTimer() {
        calendar = Calendar.getInstance()
        calendar.time = Date()

        var hour = calendar.get(Calendar.HOUR_OF_DAY).toString()
        if (hour.length == 1) {
            hour = "0$hour"
        }
        var minute = calendar.get(Calendar.MINUTE).toString()
        if (minute.length == 1) {
            minute = "0$minute"
        }
        range.setCurrentTime("$hour:$minute")

        timeHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                setTimer()
                sendEmptyMessageDelayed(1, 1000)
            }
        }
        timeHandler.sendEmptyMessage(1)
    }

    private fun setTimer() {
        calendar.time = Date()
        var month = (calendar.get(Calendar.MONTH) + 1).toString()
        if (month.length == 1) {
            month = "0$month"
        }
        var day = calendar.get(Calendar.DAY_OF_MONTH).toString()
        if (day.length == 1) {
            day = "0$day"
        }
        var hour = calendar.get(Calendar.HOUR_OF_DAY).toString()
        if (hour.length == 1) {
            hour = "0$hour"
        }
        var minute = calendar.get(Calendar.MINUTE).toString()
        if (minute.length == 1) {
            minute = "0$minute"
        }

        val date = "$hour:$minute"
        tv_date.text = date
        tv_date_week.text = "${calendar.get(Calendar.YEAR)}-$month-$day ${arr[calendar.get(Calendar.DAY_OF_WEEK) - 1]}"

        if (calendar.get(Calendar.SECOND) == 0) {
            range.setCurrentTime("$hour:$minute")
            setMeetData()
        }
    }

    var arr = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
    private var use = false

    private fun setMeetData() {
        // 过滤过期的会议
        currentList.clear()
        var remove = false
        for (meet in httpList) {
            if (System.currentTimeMillis() < meet.gmtEnd) {
                currentList.add(meet)
            } else {
                remove = true
            }
        }
        if (remove) {
            val timeList = ArrayList<RangeBar.TimeBean>()
            httpList.forEach {
                // 会议结束时间是否在当前时间之前  会议是结束了
                if (System.currentTimeMillis() >= it.gmtEnd) {
                    timeList.add(RangeBar.TimeBean(dateFormat(it.gmtStart), dateFormat(it.gmtEnd), 2))
                } else {
                    timeList.add(RangeBar.TimeBean(dateFormat(it.gmtStart), dateFormat(it.gmtEnd), 1))
                }
            }
            range.setTimeRangeList(timeList)
        }
        if (currentList.size > 0) {
            var meet = currentList[0]
            if (System.currentTimeMillis() < meet.gmtStart) {
                free(meet)
            } else if (System.currentTimeMillis() < meet.gmtEnd) {
                ing(meet)
            }
        } else {
            free(null)
        }
        if (use) {
            room_name.text = "${roomName}:占用"
        } else {
            room_name.text = "${roomName}:空闲"
        }
    }

    private fun free(meet: Meet?) {
        currentMeet=null
        use = false
        tv_time.visibility = View.GONE
        tv_creator_name.visibility = View.GONE
        tv_count_num.visibility = View.GONE
        ll_meet_set.visibility =View.GONE
        ll_delay.visibility =View.GONE
        tv_title.text = "空闲"
        if (meet == null) {
            tv_next.text = StringConstant.next_conference_cn + StringConstant.none_cn
        } else {
            tv_next.text = StringConstant.next_conference_cn + meet.name
        }
    }

    private fun ing(meet: Meet) {
        currentMeet=meet
        use = true
        tv_time.visibility = View.VISIBLE
        tv_creator_name.visibility = View.VISIBLE
        ll_meet_set.visibility = View.VISIBLE
//        tv_count_num.visibility = View.VISIBLE
        tv_time.text = "会议时间: " + dateFormat(meet.gmtStart) + "-" + dateFormat(meet.gmtEnd)
        tv_creator_name.text = "创建人: " + meet.creatorName
        tv_count_num.text = "人数 " + meet.peopleNum
        tv_title.text = meet.name
        if (currentList.size > 1) {
            tv_next.text = StringConstant.next_conference_cn + currentList[1].name
        } else {
            tv_next.text = StringConstant.next_conference_cn + StringConstant.none_cn
        }
    }

    private val httpList = ArrayList<Meet>()
    private val currentList = ArrayList<Meet>()
    @SuppressLint("CheckResult")
    private fun getMeets() {
        val deviceId = Settings.System.getString(contentResolver, Settings.System.ANDROID_ID)
        var map = HashMap<String, String>()
        map["device"] = deviceId
        RoomMeetsLoader().getRoomMeets(map).subscribe({ it ->
            httpList.clear()
            if (it.size > 0) {
                it.forEach { info ->
                    // 是否在今天以及之前
                    if (info.gmtEnd < getEndTime()) {
                        httpList.add(info)
                    }
                }
            }
            val timeList = ArrayList<RangeBar.TimeBean>()
            httpList.forEach {
                // 会议结束时间是否在当前时间之前  会议是结束了
                if (System.currentTimeMillis() >= it.gmtEnd) {
                    timeList.add(RangeBar.TimeBean(dateFormat(it.gmtStart), dateFormat(it.gmtEnd), 2))
                } else {
                    timeList.add(RangeBar.TimeBean(dateFormat(it.gmtStart), dateFormat(it.gmtEnd), 1))
                }
            }
            range.setTimeRangeList(timeList)
            setMeetData()
        }, {
            DialogUtils.ToastShow(this@MainActivity, "请求出错")
        })
    }

    private fun unbind() {
        val deviceId = Settings.System.getString(contentResolver, Settings.System.ANDROID_ID)
        var map = HashMap<String, Any>()
        map["device"] = deviceId
        UnbindMeetRoomsLoader().unbind(map).subscribe({
            DialogUtils.ToastShow(this@MainActivity, "解绑成功")
            unbindPush()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }, {
            DialogUtils.ToastShow(this@MainActivity, "解绑失败")
        })
    }

    var format: SimpleDateFormat? = null
    private fun dateFormat(time: String): String {
        var timeL = time.toLong()
        return dateFormat(timeL)
    }

    private fun dateFormat(time: Long): String {
        if (format == null) {
            format = SimpleDateFormat("HH:mm")
        }
        return format!!.format(Date(time))
    }

    private fun getEndTime(): Long {
        val todayEnd = Calendar.getInstance()
        todayEnd.set(Calendar.HOUR_OF_DAY, 23)
        todayEnd.set(Calendar.MINUTE, 59)
        todayEnd.set(Calendar.SECOND, 59)
        todayEnd.set(Calendar.MILLISECOND, 999)
        return todayEnd.timeInMillis
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun notifyChange(str: String) {
        if (str == "100") {
            getMeets()
        } else if (str == "200") {
            AlertDialog(this@MainActivity, "会议室已和本机解绑") {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }.show()
        }

    }

    private fun bindPush() {
        PushServiceFactory.getCloudPushService().bindAccount(Settings.System.getString(contentResolver, Settings.System.ANDROID_ID), object : CommonCallback {
            override fun onSuccess(p0: String?) {
                Log.e("---push------", "success")
            }

            override fun onFailed(p0: String?, p1: String?) {
                Log.e("---push------", "---" + p0)
                Log.e("---push------", "---" + p1)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindPush()
        EventBus.getDefault().unregister(this)
    }

    private fun unbindPush() {
        PushServiceFactory.getCloudPushService().unbindAccount(object : CommonCallback {
            override fun onSuccess(p0: String?) {
            }

            override fun onFailed(p0: String?, p1: String?) {
            }
        })
    }
}
