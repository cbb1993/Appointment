package com.huanhong.appointment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.app.AppCompatActivity
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import com.huanhong.appointment.bean.LoginReponseBean
import com.huanhong.appointment.net.DialogUtils
import com.huanhong.appointment.net.Fault
import com.huanhong.appointment.net.httploader.BindStateLoader
import com.huanhong.appointment.net.httploader.LoginLoader
import com.huanhong.appointment.net.httploader.MeetRoomsLoader
import com.huanhong.appointment.utils.KeyUtil
import com.huanhong.appointment.utils.SharedPreferencesUtils

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

        et_account.setText("wansheng")
        et_password.setText("123456")
        btn_login.setOnClickListener {
            if(validate()){
                Log.e("-----","---login")
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
                },{})
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
                        SharedPreferencesUtils.addData("roomName",info.fullName)
                        startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                    }
                }
            }
        },{
        })
    }


}