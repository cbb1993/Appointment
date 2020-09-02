package com.huanhong.appointment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.bumptech.glide.Glide
import com.huanhong.appointment.adapter.CommonAdapter
import com.huanhong.appointment.adapter.ViewHolder
import com.huanhong.appointment.bean.MeetDevice
import com.huanhong.appointment.net.httploader.MeetingDevicesLoader
import com.huanhong.appointment.utils.ViewUtils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.system.exitProcess

class EquipmentActivity : BaseActivity() {
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
    private lateinit var recycler_equipment: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        mLockView =
                LayoutInflater.from(this).inflate(R.layout.activity_equipment, null) as RelativeLayout
        initLockView()
        initDevicesList()
        addToWindow()
        initDate()
        initEquipments()
    }

    private fun initEquipments() {
        recycler_equipment = mLockView.findViewById(R.id.recycler_equipment)
        recycler_equipment.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        recycler_equipment.adapter = object : CommonAdapter<MeetDevice>(this, devices, R.layout.item_equipment) {
            override fun convert(holder: ViewHolder, t: MutableList<MeetDevice>) {
                val iv_icon = holder.getView<ImageView>(R.id.iv_icon)
                Glide.with(this@EquipmentActivity).load(t[holder.realPosition].remark).into(iv_icon)
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
                Glide.with(this@EquipmentActivity).load(t[holder.realPosition].remark).into(iv_icon)
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
                    removeFromWindow()
                    exitProcess(0)
                }
            }
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
            recycler_equipment.adapter?.notifyDataSetChanged()
        }, {
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
            windowManager.removeView(mLockView)
        }
    }

    fun hideSoftKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}