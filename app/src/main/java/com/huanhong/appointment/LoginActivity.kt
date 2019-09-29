package com.huanhong.appointment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.app.AppCompatActivity
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import com.huanhong.appointment.bean.LoginReponseBean
import com.huanhong.appointment.net.DialogUtils
import com.huanhong.appointment.net.Fault
import com.huanhong.appointment.net.ThrowableUtils
import com.huanhong.appointment.net.httploader.BindStateLoader
import com.huanhong.appointment.net.httploader.LoginLoader
import com.huanhong.appointment.net.httploader.MeetRoomsLoader
import com.huanhong.appointment.utils.KeyUtil
import com.huanhong.appointment.utils.SharedPreferencesUtils
import com.yanzhenjie.permission.Action
import com.yanzhenjie.permission.AndPermission

import kotlinx.android.synthetic.main.activity_login.*

/**
 * Created by 坎坎.
 * Date: 2019/7/12
 * Time: 9:32
 * describe:
 */
class LoginActivity: AppCompatActivity(){
    var show = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        et_account.setText("wansheng")
//        et_password.setText("123456")
        btn_login.setOnClickListener {
            if(validate()){
                val map = HashMap<String,String>()
                map["account"] = et_account.text.toString()
                map["password"] = et_password.text.toString()
                map["type"] = "account"
                map["terminal"] = "会议室Pad"
                map["os"] = "android"
                map["version"] = "1.0.0"
                LoginLoader().getLoginInFo(map).subscribe({
                        if(it!=null){
                            LoginReponseBean.setToken(it.token)
                            validateBind()
                        }
                },{  ThrowableUtils.ThrowableEnd(it,null)})
            }
        }

        iv_show.setOnClickListener {
            if(show){
                et_password.transformationMethod = PasswordTransformationMethod.getInstance()
            }else{
                et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            show = !show
        }

//        Handler().postDelayed({
//            requestPermission()
//        },1000)
    }

    @SuppressLint("CheckResult")
    private fun validateBind(){
        val ANDROID_ID = Settings.System.getString(contentResolver, Settings.System.ANDROID_ID)
        val map = HashMap<String,String>()
        map["device"] = ANDROID_ID
        BindStateLoader().getBindState(map).subscribe({
           getList()
        },{
                var f =   it as Fault
                if(f.status == 1005){
                    startActivity(Intent(this@LoginActivity,MeetRoomActivity::class.java))
                }
        })
    }

    private fun validate():Boolean{
        if(et_account.length()==0){
            DialogUtils.ToastShow(this,"请输入账号")
            return false
        }
        if(et_password.length()==0){
            DialogUtils.ToastShow(this,"请输入密码")
            return false
        }
        return true
    }

    @SuppressLint("CheckResult")
    private fun getList(){
        MeetRoomsLoader().rooms.subscribe({
            if(it!=null){
                it.forEach { info ->
                    if(info.deviceMac ==  Settings.System.getString(contentResolver, Settings.System.ANDROID_ID)){
                        SharedPreferencesUtils.addData("roomId",info.id)
                        SharedPreferencesUtils.addData("roomName",info.roomName)
                        startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                    }
                }
            }
        },{
        })
    }


    private fun requestPermission() {
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val p = arrayOf(
                   Manifest.permission.READ_PHONE_STATE,
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
                   .onDenied { getWindowP()
                   }.start()
       }else {
       }
   }

   private fun getWindowP() {
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           if (!Settings.canDrawOverlays(this)) {
               val intent =  Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                       Uri.parse("package:$packageName"))
               startActivityForResult(intent, 101)
           }
       }
   }




}