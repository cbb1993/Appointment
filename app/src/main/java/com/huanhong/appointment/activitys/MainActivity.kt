package com.huanhong.appointment.activitys

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.*
import android.provider.Settings
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory
import com.bumptech.glide.Glide
import com.huanhong.appointment.*
import com.huanhong.appointment.adapter.CommonAdapter
import com.huanhong.appointment.adapter.ViewHolder
import com.huanhong.appointment.bean.Meet
import com.huanhong.appointment.bean.MeetDevice
import com.huanhong.appointment.bean.RoomInfo
import com.huanhong.appointment.camera.CameraView
import com.huanhong.appointment.camera.ImageUtils
import com.huanhong.appointment.net.DialogUtils
import com.huanhong.appointment.net.ThrowableUtils
import com.huanhong.appointment.net.httploader.*
import com.huanhong.appointment.utils.FileUtil
import com.huanhong.appointment.utils.SharedPreferencesUtils
import com.huanhong.appointment.utils.ViewUtils
import com.huanhong.appointment.views.*
import com.smbd.peripheral.SmbdLed
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by 坎坎.
 * Date: 2019/7/10
 * Time: 13:29
 * describe:
 */
class MainActivity : BaseActivity() {
    lateinit var timeHandler: Handler
    lateinit var calendar: Calendar

    private var roomName = ""
    private var currentMeet: Meet? = null
    private var delayType = 0 // 是否支持延时 1 不支持
    private var deviceIds = "" // 该会议室有的设备列表

    companion object {
        var needAudit = 0 // 1 需要审核  2 不需要审核
    }

    // 是否是灯光设备
    private var isDevice = true

    private lateinit var mSmbdLed: SmbdLed

    // 是否已经初始化了右上角的设备列表
    private var initDevice = true

    lateinit var meetingCacheFile: File

    var time = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 获得设备类型
        val deviceType = ConfigPopwindow.getDeviceType()
        isDevice = deviceType == 0
        if (isDevice) {
            mSmbdLed = SmbdLed()
        }
        initView()
        setMeetData()
        initTimer()

        meetingCacheFile = File(BaseApplication.path)
        if (!meetingCacheFile.exists()) {
            meetingCacheFile.mkdirs()
        }

        tv_setting!!.setOnClickListener {
            unBind = true
            ll_lock.visibility = View.VISIBLE
        }
        bindPush()
        roomName = SharedPreferencesUtils.readData("roomName")!!

        tv_order!!.setOnClickListener {
            startActivity(Intent(this@MainActivity, OrderLoginActivity::class.java))
        }

        tv_end!!.setOnClickListener {
            if (currentMeet != null) {
                val map = HashMap<String, Any?>()
                map["id"] = currentMeet?.id
                map["state"] = 2
                EndMeetMeetRoomsLoader().request(map).subscribe({
                    DialogUtils.ToastShow(this@MainActivity, "会议已经结束")
                    getMeets()
                }, {
                    ThrowableUtils.ThrowableEnd(it, null)
                })
            }
        }

        tv_delay!!.setOnClickListener {
            SelectPopwindow(this, tv_delay) { it ->
                delay(it)
            }
        }


        range!!.setMeetClickListner { it ->
            if (it < httpList.size) {
                MeetPop(this@MainActivity, httpList[it]).show(pop_line)
            }
        }

        recycler_devices!!.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayout.HORIZONTAL, false)
        recycler_devices!!.adapter = object : CommonAdapter<MeetDevice>(this, devices, R.layout.item_icon) {
            override fun convert(holder: ViewHolder, t: MutableList<MeetDevice>) {
                val iv_icon = holder.getView<ImageView>(R.id.iv_icon)

                Glide.with(this@MainActivity).load(t[holder.realPosition].remark).into(iv_icon)
            }
        }

        getMeets()
    }

    @SuppressLint("CheckResult")
    private fun delay(time: String) {
        val map = HashMap<String, Any?>()
        map["id"] = currentMeet?.id
        map["delayTimeStr"] = time
        map["roomId"] = SharedPreferencesUtils.readData("roomId")
        DelayMeetMeetRoomsLoader().request(map).subscribe({
            DialogUtils.ToastShow(this@MainActivity, "延时成功")
            getMeets()
        }, {
            ThrowableUtils.ThrowableEnd(it, null)
        })
    }

    override fun checkNetwork(isConnected: Boolean) {
        super.checkNetwork(isConnected)
        if (isConnected) {
            rl_net?.visibility = View.GONE
            getMeets()
        } else {
            rl_net?.visibility = View.VISIBLE
        }
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
        range!!.setCurrentTime("$hour:$minute")

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
        tv_date!!.text = date
        tv_date_week!!.text = "${calendar.get(Calendar.YEAR)}-$month-$day ${arr[calendar.get(Calendar.DAY_OF_WEEK) - 1]}"

        val second = calendar.get(Calendar.SECOND)

        if (calendar.get(Calendar.SECOND) == 0) {
            range!!.setCurrentTime("$hour:$minute")
            setMeetData()
        }
        // 是否是凌晨1点 清除 crash 日志 和 会议列表日志
        if (calendar.get(Calendar.HOUR_OF_DAY) == 1
                && calendar.get(Calendar.MINUTE) == 0
                && second == 0) {
            for (f in meetingCacheFile.listFiles()) {
                f.delete()
            }
        }
        time = "$date:$second"

//        // 每10秒刷新
        if (second % 10 == 0) {
            getMeets()
        }

    }

    var arr = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
    private var use = false

    private fun setMeetData() {
        // 过滤过期的会议
        currentList.clear()
        var remove = false
        for (meet in httpList) {
            if (System.currentTimeMillis() < meet.gmtEnd && (meet.state == 1 || meet.state == 3)) {
                currentList.add(meet)
            } else {
                remove = true
            }
        }
        if (remove) {
            val timeList = ArrayList<RangeBar.TimeBean>()
            httpList.forEach {
                // 会议结束时间是否在当前时间之前  会议是结束了
                if (System.currentTimeMillis() >= it.gmtEnd || it.state == 2) {
                    timeList.add(RangeBar.TimeBean(dateFormat(it.gmtStart), dateFormat(it.gmtEnd), 2))
                } else {
                    timeList.add(RangeBar.TimeBean(dateFormat(it.gmtStart), dateFormat(it.gmtEnd), 1))
                }
            }
            range!!.setTimeRangeList(timeList)
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
            room_name!!.text = "${roomName}:会议中"
        } else {
            room_name!!.text = "${roomName}:可使用"
        }
    }

    private fun free(meet: Meet?) {
        currentMeet = null
        use = false
        tv_time!!.visibility = View.GONE
        tv_creator_name!!.visibility = View.GONE
        ll_meet_set!!.visibility = View.GONE
        tv_title!!.text = "空闲"
        if (meet == null) {
            tv_next!!.text = StringConstant.next_conference_cn + StringConstant.none_cn
        } else {
            tv_next!!.text = StringConstant.next_conference_cn + meet.name
        }

        if (isDevice) {
            // 获得空闲时候的灯光颜色
            setLightColor(ConfigPopwindow.getFreeLightType())
        }
        rl_root.setBackgroundResource(R.mipmap.main_bg)
    }

    private fun setLightColor(type: Int) {
        when (type) {
            0 -> mSmbdLed.onGreen(true)
            1 -> mSmbdLed.onRed(true)
            2 -> mSmbdLed.onYellow(true)
            3 -> mSmbdLed.onClose()
        }
    }

    private fun ing(meet: Meet) {
        currentMeet = meet
        use = true
        tv_time?.visibility = View.VISIBLE
        tv_creator_name?.visibility = View.VISIBLE
        if (delayType == 1) {
            tv_delay?.visibility = View.GONE
        } else {
            tv_delay?.visibility = View.VISIBLE
        }
        ll_meet_set?.visibility = View.VISIBLE

        tv_time?.text = "会议时间: " + dateFormat(meet.gmtStart) + "-" + dateFormat(meet.gmtEnd)
        tv_creator_name?.text = "创建人: " +meet.deptName+ meet.creatorName
        tv_title?.text = meet.name
        if (currentList.size > 1) {
            tv_next?.text = StringConstant.next_conference_cn + currentList[1].name
        } else {
            tv_next?.text = StringConstant.next_conference_cn + StringConstant.none_cn
        }
        if (isDevice) {
            // 获得会议中时候的灯光颜色
            setLightColor(ConfigPopwindow.getIngLightType())
        }

        meetingType("" + meet.typeId)
    }

    private val httpList = ArrayList<Meet>()
    private val currentList = ArrayList<Meet>()
    private var lastData: RoomInfo? = null

    @SuppressLint("CheckResult")
    private fun getMeets() {
        FileUtil.appendMethodB("$time---请求开始\n");
        val deviceId = Settings.System.getString(contentResolver, Settings.System.ANDROID_ID)
        var map = HashMap<String, String>()
        map["device"] = deviceId
        RoomMeetsLoader().getRoomMeets(map).subscribe({ it ->
//            val compareMeeting = compareMeeting(lastData, it)
//            Log.e("---000--","------$compareMeeting")
//            // 返回true 表示一致 无需更新
//            if(compareMeeting){
//                return@subscribe
//            }
//            Log.e("---111--","------")
            lastData = it
            httpList.clear()
            // 配置
            delayType = it.configuration.delayType
            needAudit = it.configuration.needAudit
            deviceIds = it.configuration.deviceIds

            if (initDevice) {
                getDevices()
                initDevice = false
            }


            // 写入log
            FileUtil.appendMethodB(time + "---请求结束-->" + it.list.size + "条会议\n")

            if (it.list.size > 0) {
                it.list.forEach { info ->
                    // 是否在今天
                    if (info.gmtEnd < getEndTime() && info.gmtStart > getStartTime()) {
                        httpList.add(info)
                    }
                }
            }

            val timeList = ArrayList<RangeBar.TimeBean>()
            httpList.forEach {
                // 会议结束时间是否在当前时间之前  会议是结束了
                if (System.currentTimeMillis() >= it.gmtEnd || it.state == 2) {
                    timeList.add(RangeBar.TimeBean(dateFormat(it.gmtStart), dateFormat(it.gmtEnd), 2))
                } else {
                    timeList.add(RangeBar.TimeBean(dateFormat(it.gmtStart), dateFormat(it.gmtEnd), 1))
                }
            }
            range!!.setTimeRangeList(timeList)
            setMeetData()
        }, {
//            ThrowableUtils.ThrowableEnd(it, null);
            FileUtil.appendMethodB("$time---请求结束--请求出错---"+ThrowableUtils.ThrowableEn3(it)+"\n")

//            DialogUtils.ToastShow(this@MainActivity, "请求出错")
        })
    }


    @SuppressLint("CheckResult")
    private fun signIn(id: String) {
        if (currentMeet == null) {
            return
        }
        var map = HashMap<String, Any>()
        map["mId"] = currentMeet!!.id
        map["userId"] = id
        SignInLoader().request(map).subscribe({
            updateCheckState(2)
        }, {
            val msg = ThrowableUtils.ThrowableEnd2(it)
            errorMsg = msg
            updateCheckState(3)
        })
    }


    private val devices = ArrayList<MeetDevice>()

    @SuppressLint("CheckResult")
    private fun getDevices() {
        var map = HashMap<String, Any>()
        map["key"] = "meeting_device"
        MeetingDevicesLoader().request(map).subscribe({
            devices.clear()
            it.forEach { d ->
                if (deviceIds.indexOf(d.id) > -1) {
                    devices.add(d)
                }
            }
            recycler_devices?.adapter?.notifyDataSetChanged()
        }, {
        })
    }

    @SuppressLint("CheckResult")
    private fun unbind() {
        val deviceId = Settings.System.getString(contentResolver, Settings.System.ANDROID_ID)
        var map = HashMap<String, Any>()
        map["device"] = deviceId
        UnbindMeetRoomsLoader().unbind(map).subscribe({
            //            DialogUtils.ToastShow(this@MainActivity, "解绑成功")
            SplashActivity.clearLogin()
            unbindPush()
            removeFromWindow()
            System.exit(0)
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))

        }, {
            ThrowableUtils.ThrowableEnd(it, null)
//            DialogUtils.ToastShow(this@MainActivity, "解绑失败")
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

    private fun getStartTime(): Long {
        val todayS = Calendar.getInstance()
        todayS.set(Calendar.HOUR_OF_DAY, 0)
        todayS.set(Calendar.MINUTE, 0)
        todayS.set(Calendar.SECOND, 0)
        todayS.set(Calendar.MILLISECOND, 0)
        return todayS.timeInMillis
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
            }

            override fun onFailed(p0: String?, p1: String?) {
            }
        })
    }

    override fun onDestroy() {
        removeFromWindow()
        super.onDestroy()
        unbindPush()
    }

    private fun unbindPush() {
        PushServiceFactory.getCloudPushService().unbindAccount(object : CommonCallback {
            override fun onSuccess(p0: String?) {
            }

            override fun onFailed(p0: String?, p1: String?) {
            }
        })
    }

    private var mLockView: RelativeLayout? = null
    private var tv_setting: TextView? = null
    private var tv_order: TextView? = null
    private var tv_end: TextView? = null
    private var tv_delay: TextView? = null
    private var tv_date: TextView? = null
    private var room_name: TextView? = null
    private var tv_date_week: TextView? = null
    private var tv_time: TextView? = null
    private var ll_meet_set: View? = null
    private var tv_creator_name: TextView? = null
    private var tv_next: TextView? = null
    private var tv_title: TextView? = null
    private var range: RangeBar? = null
    private var recycler_devices: RecyclerView? = null
    private var pop_line: View? = null
    private var rl_net: View? = null

    //
    private var rl_error: View? = null


    // 密码锁
    private lateinit var view_lock: View
    private lateinit var ll_lock: View
    private lateinit var tv_confirm: View
    private lateinit var tv_cancel: View
    private lateinit var et_password: EditText
    private val password = "123"

    // 解绑
//    private lateinit var tv_unbind_cancel: View
//    private lateinit var tv_unbind_confirm: View
//    private lateinit var rl_unbind: View

    private var unBind = false

    private lateinit var mCameraView: CameraView
    private lateinit var rl_camera: View
    private lateinit var rl_state: View
    private lateinit var iv_pic: ImageView
    private lateinit var tv_state: TextView

    private lateinit var rl_root: View


    private fun initView() {
        mLockView =
                LayoutInflater.from(this).inflate(R.layout.activity_main, null) as RelativeLayout
        rl_root = mLockView!!.findViewById(R.id.rl_root)
        tv_setting = mLockView!!.findViewById(R.id.tv_setting)
        tv_order = mLockView!!.findViewById(R.id.tv_order)
        tv_end = mLockView!!.findViewById(R.id.tv_end)
        tv_delay = mLockView!!.findViewById(R.id.tv_delay)
        range = mLockView!!.findViewById(R.id.range)
        recycler_devices = mLockView!!.findViewById(R.id.recycler_devices)
        pop_line = mLockView!!.findViewById(R.id.pop_line)
        tv_date = mLockView!!.findViewById(R.id.tv_date)
        tv_date_week = mLockView!!.findViewById(R.id.tv_date_week)
        room_name = mLockView!!.findViewById(R.id.room_name)
        tv_time = mLockView!!.findViewById(R.id.tv_time)
        tv_creator_name = mLockView!!.findViewById(R.id.tv_creator_name)
        ll_meet_set = mLockView!!.findViewById(R.id.ll_meet_set)
        tv_title = mLockView!!.findViewById(R.id.tv_title)
        tv_next = mLockView!!.findViewById(R.id.tv_next)
        rl_net = mLockView!!.findViewById(R.id.rl_net)
        rl_error = mLockView!!.findViewById(R.id.rl_error)
//        rl_unbind =mLockView!!. findViewById(R.id.rl_unbind)
//        tv_unbind_cancel =mLockView!!. findViewById(R.id.tv_unbind_cancel)
//        tv_unbind_confirm =mLockView!!. findViewById(R.id.tv_unbind_confirm)
        initLockView()
        addToWindow()


//        tv_unbind_cancel.setOnClickListener {
//            rl_unbind.visibility = View.GONE
//        }
//
//        tv_unbind_confirm.setOnClickListener {
//            rl_unbind.visibility = View.GONE
//            unbind()
//        }
        rl_state = mLockView!!.findViewById(R.id.rl_state)
        iv_pic = mLockView!!.findViewById(R.id.iv_pic)
        tv_state = mLockView!!.findViewById(R.id.tv_state)

        mCameraView = mLockView!!.findViewById(R.id.view_camera)
        rl_camera = mLockView!!.findViewById(R.id.rl_camera)
        mCameraView.setDismissCallback {
            rl_camera.visibility = View.INVISIBLE
        }
        mCameraView.setTakePhotoStateCallback {
            // 开始上传
            checkPhoto(ImageUtils.lastPath)
        }
        val tv_face_tip = mLockView!!.findViewById<TextView>(R.id.tv_face_tip)

        mLockView!!.findViewById<View>(R.id.tv_camera).setOnClickListener {
            if (use) {
//                rl_camera.visibility= View.VISIBLE
                tv_face_tip.visibility = View.VISIBLE
                Handler().postDelayed(Runnable {
                    tv_face_tip.visibility = View.GONE
                    mCameraView.takePhoto()
                }, 1000)

            }
        }

        rl_camera.visibility = View.INVISIBLE
    }

    private fun updateCheckState(flag: Int) {
        when (flag) {
            1 -> {
                rl_state.visibility = View.VISIBLE
                Glide.with(this).load(ImageUtils.lastPath).into(iv_pic)
                tv_state.text = "正在签到..."
            }
            2 -> {
                handler.sendEmptyMessageDelayed(1, 2000)
                tv_state.text = "签到成功"
            }
            3 -> {
                handler.sendEmptyMessageDelayed(1, 2000)
                tv_state.text = errorMsg
            }
            4 -> {
                rl_state.visibility = View.GONE;
            }
        }

    }

    @SuppressLint("CheckResult")
    private fun meetingType(id: String) {
        MeetTypeLoader().request(id).subscribe({
            if (it.themeId == 1) {
                rl_root.setBackgroundResource(R.mipmap.red_bg)
            } else {
                rl_root.setBackgroundResource(R.mipmap.main_bg)
            }
        }, {})
    }


    @SuppressLint("HandlerLeak")
    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            updateCheckState(4)
        }
    }


    private var errorMsg = ""

    @SuppressLint("CheckResult")
    fun checkPhoto(path2: String) {
        val file = File(path2)
        ImageUtils.lastPath = file.absolutePath
        updateCheckState(1)
        val filePart = MultipartBody.Part.createFormData(
                "file",
                file.name,
                RequestBody.create(MediaType.parse("image/*"), file)
        )
        PhotoCheckLoader().request(filePart).subscribe({
            signIn(it.id)
            // 清除所有缓存数据
            for (f in file.parentFile.listFiles()) {
                f.delete()
            }
        }, {
            val msg = ThrowableUtils.ThrowableEnd2(it)
            errorMsg = msg
            updateCheckState(3)
            // 清除所有缓存数据
            for (f in file.parentFile.listFiles()) {
                f.delete()
            }
        })
    }


    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), value)
    }

    // 初始化密码框
    private fun initLockView() {
        view_lock = mLockView!!.findViewById(R.id.view_lock)
        ll_lock = mLockView!!.findViewById(R.id.ll_lock)
        tv_confirm = mLockView!!.findViewById(R.id.tv_confirm)
        tv_cancel = mLockView!!.findViewById(R.id.tv_cancel)
        et_password = mLockView!!.findViewById(R.id.et_password) as EditText

        view_lock.setOnLongClickListener {
            unBind = false
            ll_lock.visibility = View.VISIBLE
            true
        }
        tv_cancel.setOnClickListener {
            hideSoftKeyboard(tv_cancel)
            ll_lock.visibility = View.INVISIBLE
            et_password.setText("")
            unBind = false
        }
        tv_confirm.setOnClickListener {
            if (et_password.length() != 0) {
                if (et_password.text.toString() == password) {
                    if (unBind) {
                        unbind()
                    } else {
                        SplashActivity.clearLogin()
                        removeFromWindow()
                        System.exit(0)
                    }
                }
            }
        }
    }

    private fun addToWindow() {
        if (mLockView != null && mLockView?.parent == null) {
            windowManager.addView(mLockView, getLockLayoutParams())
            ViewUtils.setSystemUiGone(mLockView)
        }
    }

    private fun removeFromWindow() {
        timeHandler.removeCallbacksAndMessages(null)
        handler.removeCallbacksAndMessages(null)
        if (mLockView != null && mLockView?.parent == null) {
            windowManager.removeView(mLockView)
        }
    }


    // 获得锁屏的设置
    private fun getLockLayoutParams(): WindowManager.LayoutParams {
        val mLayoutParams = WindowManager.LayoutParams()
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
        mLayoutParams.flags = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_FULLSCREEN)
        return mLayoutParams
    }

    fun hideSoftKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun luban(file: File) {
        Luban.with(this)
                .load(file) // 传人要压缩的图片列表
                .ignoreBy(100) // 忽略不压缩图片的大小
                .setTargetDir(file.parent) // 设置压缩后文件存储位置
                .setCompressListener(object : OnCompressListener {
                    //设置回调
                    override fun onStart() {}
                    override fun onSuccess(file: File) {
                        Log.e("-------", "---------" + file.absolutePath)
                    }

                    override fun onError(e: Throwable) {}
                }).launch()
    }

}
