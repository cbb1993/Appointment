package com.huanhong.appointment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import com.google.gson.Gson
import com.huanhong.appointment.bean.AppVersion
import com.huanhong.appointment.bean.LoginReponseBean
import com.huanhong.appointment.net.DialogUtils
import com.huanhong.appointment.net.Fault
import com.huanhong.appointment.net.ThrowableUtils
import com.huanhong.appointment.net.httploader.BindStateLoader
import com.huanhong.appointment.net.httploader.LoginLoader
import com.huanhong.appointment.net.httploader.MeetRoomsLoader
import com.huanhong.appointment.net.httploader.UpdateLoader
import com.huanhong.appointment.utils.KeyUtil
import com.huanhong.appointment.utils.SharedPreferencesUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.yanzhenjie.permission.Action
import com.yanzhenjie.permission.AndPermission

import kotlinx.android.synthetic.main.activity_login.*
import java.io.File
import java.lang.reflect.Type

/**
 * Created by 坎坎.
 * Date: 2019/7/12
 * Time: 9:32
 * describe:
 */
class LoginActivity : BaseActivity() {
//    companion object {
//        // 7 MainActivity 8 SeatActivity  9 EquipmentActivity
//        val loginToActivity = 7
//    }

    var show = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
//        et_account.setText("scpad")
//        et_password.setText("123456")
        btn_login.setOnClickListener {
            if (validate()) {
                val map = HashMap<String, String>()
                map["account"] = et_account.text.toString()
                map["password"] = et_password.text.toString()
                map["type"] = "account"
                map["terminal"] = "会议室Pad"
                map["os"] = "android"
                map["version"] = "1.0.0"
                LoginLoader().getLoginInFo(map).subscribe({
                    if (it != null) {
                        LoginReponseBean.setToken(it.token)
                        validateBind()
                    }
                }, { ThrowableUtils.ThrowableEnd(it, null) })

            }
        }

        iv_show.setOnClickListener {
            if (show) {
                et_password.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            show = !show
        }

        Handler().postDelayed({
            requestPermission()
        }, 1000)


        tv_update.setOnClickListener {
            UpdateLoader().version.subscribe({
                if (it != null) {
                    if (it.version == BuildConfig.VERSION_NAME) {
                        DialogUtils.ToastShow(this, "当前为最新版本")
                    } else {
                        // 更新
                        val o = Gson().fromJson<AppVersion.Resource>(it.resource)
                        downLoad(o.path)
                    }
                }
            }, {
                ThrowableUtils.ThrowableEnd(it, null)
            })
        }
    }

    inline fun <reified T : Any> Gson.fromJson(json: String): T {
        return Gson().fromJson(json, T::class.java)
    }

    @SuppressLint("CheckResult")
    private fun validateBind() {
        val ANDROID_ID = Settings.System.getString(contentResolver, Settings.System.ANDROID_ID)
        val map = HashMap<String, String>()
        map["device"] = ANDROID_ID
        BindStateLoader().getBindState(map).subscribe({
            getList(it.flatsTag)
        }, {
            var f = it as Fault
            if (f.status == 1005) {
                startActivity(Intent(this@LoginActivity, MeetRoomActivity::class.java))
            }
        })
    }

    private fun validate(): Boolean {
        if (et_account.length() == 0) {
            DialogUtils.ToastShow(this, "请输入账号")
            return false
        }
        if (et_password.length() == 0) {
            DialogUtils.ToastShow(this, "请输入密码")
            return false
        }
        return true
    }

    @SuppressLint("CheckResult")
    private fun getList(flatsTag:Int) {
        val map = HashMap<String, String>()
        map["flatsTag"] = "" + flatsTag
        MeetRoomsLoader().getRooms(map).subscribe({
            var b = false
            it?.forEach { info ->
                if (info.deviceMac == Settings.System.getString(contentResolver, Settings.System.ANDROID_ID)) {
                    b = true
                    SharedPreferencesUtils.addData("roomId", info.id)
                    SharedPreferencesUtils.addData("roomName", info.roomName)
                }
            }
            if (b) {
                when(flatsTag){
                    7-> startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    8-> startActivity(Intent(this@LoginActivity, SeatActivity::class.java))
                    9-> startActivity(Intent(this@LoginActivity, EquipmentActivity::class.java))
                }
            } else {
                startActivity(Intent(this@LoginActivity, MeetRoomActivity::class.java))
            }
        }, {
        })
    }


    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val p = arrayOf(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WAKE_LOCK,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_SETTINGS)

            AndPermission.with(this)
                    .runtime()
                    .permission(p)
                    // 用户给权限了
                    .onGranted { getWindowP() }
                    .onDenied {
                        getWindowP()
                    }.start()
        } else {
        }
    }

    private fun getWindowP() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName"))
                startActivityForResult(intent, 101)
            }
        }
    }

    private fun downLoad(url: String) {
        val file = File(Environment.getExternalStorageDirectory(), "appointment.apk")
        val fileTemp = File(Environment.getExternalStorageDirectory(), "appointment.apk.temp")
        if (file.exists()) {
            file.delete()
        }
        if (fileTemp.exists()) {
            fileTemp.delete()
        }
        FileDownloader.getImpl().create(url)
                .setPath(file.path)
                .setListener(object : FileDownloadListener() {
                    override fun warn(task: BaseDownloadTask) {
                    }

                    override fun completed(task: BaseDownloadTask) {
                        DialogUtils.ToastShow(this@LoginActivity, "下载完成")
                        install(file)
                    }

                    override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun error(task: BaseDownloadTask?, e: Throwable) {
                        DialogUtils.ToastShow(this@LoginActivity, e.message)
                    }

                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                        val p = (soFarBytes.toFloat() / totalBytes.toFloat()) * 100
                        DialogUtils.ToastShow(this@LoginActivity, "正在下载...${p.toInt()}%")
                    }

                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }

                }).start()
    }

    private fun install(file: File) {
        AndPermission
                .with(this)
                .install().file(file).start()
    }

//    private fun install(apkPath:String){
//        try {
//            /**
//             * provider
//             * 处理android 7.0 及以上系统安装异常问题
//             */
//            val file =  File(apkPath)
//            val install =  Intent()
//            install.action = Intent.ACTION_VIEW
//            install.addCategory(Intent.CATEGORY_DEFAULT)
//            install.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            if (Build.VERSION.SDK_INT >= 24) {
//                val p = arrayOf(
//                        Manifest.permission.REQUEST_INSTALL_PACKAGES)
//                AndPermission.with(this)
//                        .runtime()
//                        .permission(p)
//                        // 用户给权限了
//                        .onGranted {
//                            val apkUri = FileProvider.getUriForFile(this, "com.huanhong.appointment", file)//在AndroidManifest中的android:authorities值
//                            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)//添加这一句表示对目标应用临时授权该Uri所代表的文件
//                            install.setDataAndType(apkUri, "application/vnd.android.package-archive")
//                            startActivity(install)
//                        }
//                        .onDenied {
//                        }.start()
//            } else {
//                install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
//                startActivity(install)
//            }
//
//        } catch (e:Exception ) {
//            Log.e("---","---"+e.message)
//            DialogUtils.ToastShow(this@LoginActivity,"文件解析失败")
//        }
//    }

}