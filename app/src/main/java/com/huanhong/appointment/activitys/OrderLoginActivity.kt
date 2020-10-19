package com.huanhong.appointment.activitys

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import com.huanhong.appointment.R
import com.huanhong.appointment.net.DialogUtils
import com.huanhong.appointment.net.ThrowableUtils
import com.huanhong.appointment.net.httploader.GetCodeRoomsLoader
import com.huanhong.appointment.net.httploader.OrderLoginLoader
import kotlinx.android.synthetic.main.activity_order_login.*

/**
 * Created by 坎坎.
 * Date: 2019/7/12
 * Time: 9:32
 * describe:
 */
class OrderLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_login)

        btn_login.setOnClickListener {
            if(validate()){
                val map = HashMap<String,String>()
                map["mobile"] = et_account.text.toString()
                map["code"] = et_password.text.toString()
                OrderLoginLoader().getLoginInFo(map).subscribe({
                        if(it!=null){
                            val user = it.user
                            startActivity(Intent(this@OrderLoginActivity, OrderActivity::class.java)
                                    .putExtra("token",it.token)
                                    .putExtra("id",user.id))
                        }
                },{
                    ThrowableUtils.ThrowableEnd(it,null)
                })
            }
        }

        tv_code.setOnClickListener {
            if(et_account.length()==0){
                DialogUtils.ToastShow(this,"请输入账号")
            }else{
                getCode(et_account.text.toString())
            }

        }
    }
    val handler = @SuppressLint("HandlerLeak")
    object :Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 1){
                time --
                if(time > 0){
                    tv_code.text = "${time}秒"
                    sendEmptyMessageDelayed(1,1000)
                }else{
                    time  = 60
                    tv_code.text = "验证码"
                }
            }
        }
    }
    private var time  =60

    @SuppressLint("CheckResult")
    private fun getCode(mobile:String){
        if(time==60){
            handler.sendEmptyMessage(1)
            val  map = HashMap<String,Any?>()
            map["mobile"] =mobile
            map["type"] = 2
            GetCodeRoomsLoader().request(map).subscribe({
                DialogUtils.ToastShow(this@OrderLoginActivity, "发送成功")
            }, {
                ThrowableUtils.ThrowableEnd(it,null)
            })
        }
    }


    private fun validate():Boolean{
        if(et_account.length()==0){
            DialogUtils.ToastShow(this,"请输入账号")
            return false
        }
        if(et_password.length()==0){
            DialogUtils.ToastShow(this,"请输入验证码")
            return false
        }
        return true
    }

}