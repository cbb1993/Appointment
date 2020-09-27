package com.huanhong.appointment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory
import com.bumptech.glide.Glide
import com.huanhong.appointment.adapter.CommonAdapter
import com.huanhong.appointment.adapter.ViewHolder
import com.huanhong.appointment.bean.MeetDevice
import com.huanhong.appointment.bean.Seat
import com.huanhong.appointment.net.ThrowableUtils
import com.huanhong.appointment.net.httploader.MeetingDevicesLoader
import com.huanhong.appointment.net.httploader.QRCodeLoader
import com.huanhong.appointment.net.httploader.SeatsLoader
import com.huanhong.appointment.net.httploader.UnbindMeetRoomsLoader
import com.huanhong.appointment.utils.QRCodeUtil
import com.huanhong.appointment.utils.SharedPreferencesUtils
import com.huanhong.appointment.utils.ViewUtils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.system.exitProcess

class SeatActivity : BaseActivity() {
    private lateinit var mLockView: RelativeLayout

    // 密码锁
    private lateinit var view_lock: View
    private lateinit var ll_lock: View
    private lateinit var tv_confirm: View
    private lateinit var tv_cancel: View
    private lateinit var et_password: EditText
    private val password = "123"

    // 右上角设备图片
    private lateinit var recycler_devices: RecyclerView
    private val devices = ArrayList<MeetDevice>()

    // 时间
    private lateinit var tv_date: TextView
    private lateinit var tv_date_week: TextView
    private lateinit var calendar: Calendar
    private lateinit var timeHandler: Handler
    var arr = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")

    // 器材
    private lateinit var recycler_seat: RecyclerView
    private val seats = ArrayList<Seat>()

    // 二维码
    private lateinit var iv_qrcode :ImageView

    // 解绑
    private lateinit var tv_setting: TextView
    private var unBind = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        mLockView =
                LayoutInflater.from(this).inflate(R.layout.activity_seat, null) as RelativeLayout
        initLockView()
        initDevicesList()
        addToWindow()
        initDate()
        initSeats()

        getQRCode()
        handler.sendEmptyMessage(1)
    }

    private fun initSeats() {
        recycler_seat = mLockView.findViewById(R.id.recycler_seat)
        recycler_seat.layoutManager = GridLayoutManager(this, 6)
        recycler_seat.adapter = object : CommonAdapter<Seat>(this, seats, R.layout.item_seat) {
            override fun convert(holder: ViewHolder, t: MutableList<Seat>) {
//               1.可用 2.占用中 3.锁定
                val iv_seat = holder.getView<ImageView>(R.id.iv_seat)
                when(t[holder.realPosition].state){
                    1 -> iv_seat.setImageResource(R.mipmap.icon_seat_normal)
                    2 -> iv_seat.setImageResource(R.mipmap.icon_seat_select)
                    3 -> iv_seat.setImageResource(R.mipmap.icon_seat_forbid)
                }
            }
        }
    }

    // 时间
    private fun initDate() {
        tv_date = mLockView.findViewById(R.id.tv_date)
        tv_date_week = mLockView.findViewById(R.id.tv_date_week)
        calendar = Calendar.getInstance()
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

    // 初始化右上角设备列表
    private fun initDevicesList() {
        recycler_devices = mLockView.findViewById(R.id.recycler_devices)
        recycler_devices.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        recycler_devices.adapter = object : CommonAdapter<MeetDevice>(this, devices, R.layout.item_icon) {
            override fun convert(holder: ViewHolder, t: MutableList<MeetDevice>) {
                val iv_icon = holder.getView<ImageView>(R.id.iv_icon)
                Glide.with(this@SeatActivity).load(t[holder.realPosition].remark).into(iv_icon)
            }
        }
        getDevices()
    }

    // 初始化密码框
    private fun initLockView() {

        view_lock = mLockView.findViewById(R.id.view_lock)
        ll_lock = mLockView.findViewById(R.id.ll_lock)
        tv_confirm = mLockView.findViewById(R.id.tv_confirm)
        tv_cancel = mLockView.findViewById(R.id.tv_cancel)
        et_password = mLockView.findViewById(R.id.et_password) as EditText
        view_lock.setOnLongClickListener {
            ll_lock.visibility = View.VISIBLE
            true
        }
        tv_cancel.setOnClickListener {
            hideSoftKeyboard(tv_cancel)
            ll_lock.visibility = View.INVISIBLE
            et_password.setText("")
        }
        tv_confirm.setOnClickListener {
            if (et_password.length() != 0) {
                if (et_password.text.toString() == password) {
                    if (unBind) {
                        unbind()
                    } else {
                        removeFromWindow()
                        exitProcess(0)
                    }
                }
            }
        }
        // 解绑
        tv_setting = mLockView.findViewById(R.id.tv_setting)
        tv_setting.setOnClickListener {
            unBind =true
            ll_lock.visibility = View.VISIBLE
        }
    }


    @SuppressLint("CheckResult")
    private fun getDevices() {
        val map = HashMap<String, Any>()
        map["key"] = "meeting_device"
        MeetingDevicesLoader().request(map).subscribe({
            devices.clear()
            it.forEach { d ->
                devices.add(d)
            }
            recycler_devices.adapter?.notifyDataSetChanged()
        }, {
        })
    }

    @SuppressLint("CheckResult")
    private fun getQRCode() {
        iv_qrcode = mLockView.findViewById(R.id.iv_qrcode)
        val map = HashMap<String, Any>()
        map["roomId"] = SharedPreferencesUtils.readData("roomId")
        QRCodeLoader().request(map).subscribe({
            iv_qrcode.setImageBitmap(QRCodeUtil.createQRCodeBitmap(it,800, 800))
        }, {
        })
    }

    @SuppressLint("CheckResult")
    private fun getSeats() {
        SeatsLoader().request(SharedPreferencesUtils.readData("roomId")).subscribe({
            seats.clear()
            seats.addAll(it)
            recycler_seat.adapter?.notifyDataSetChanged()
        }, {
        })
    }

    @SuppressLint("CheckResult")
    private fun unbind() {
        val deviceId = Settings.System.getString(contentResolver, Settings.System.ANDROID_ID)
        var map = java.util.HashMap<String, Any>()
        map["device"] = deviceId
        UnbindMeetRoomsLoader().unbind(map).subscribe({
            //            DialogUtils.ToastShow(this@MainActivity, "解绑成功")
            removeFromWindow()
            System.exit(0)
            startActivity(Intent(this@SeatActivity, LoginActivity::class.java))

        }, {
            ThrowableUtils.ThrowableEnd(it, null)
        })
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
        tv_date!!.text = date
        tv_date_week!!.text = "${calendar.get(Calendar.YEAR)}-$month-$day ${arr[calendar.get(Calendar.DAY_OF_WEEK) - 1]}"
    }


    private fun addToWindow() {
        if (mLockView.parent == null) {
            windowManager.addView(mLockView, ViewUtils.getLockLayoutParams())
            ViewUtils.setSystemUiGone(mLockView)
        }
    }

    private fun removeFromWindow() {
        if (mLockView.parent == null) {
            timeHandler.removeCallbacksAndMessages(null)
            handler.removeCallbacksAndMessages(null)
            windowManager.removeView(mLockView)
        }
    }

    fun hideSoftKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    @SuppressLint("HandlerLeak")
    val handler = object :Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            getSeats()
            sendEmptyMessageDelayed(1,15_000)
        }
    }
}