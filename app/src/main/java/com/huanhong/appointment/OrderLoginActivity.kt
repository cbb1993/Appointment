package com.huanhong.appointment

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.huanhong.appointment.net.DialogUtils
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
                            startActivity(Intent(this@OrderLoginActivity,OrderActivity::class.java)
                                    .putExtra("token",it.token))
                        }
                },{})
            }
        }
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

}