package com.huanhong.appointment.net.httploader;

import com.huanhong.appointment.bean.LoginReponseBean;
import com.huanhong.appointment.constant.Constant;
import com.huanhong.appointment.net.BaseResponse;
import com.huanhong.appointment.net.ObjectLoader;
import com.huanhong.appointment.net.PayLoad;
import com.huanhong.appointment.net.RetrofitServiceManager;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by dhy
 * Date: 2019/5/9
 * Time: 11:18
 * describe:
 */
public class OrderLoginLoader extends ObjectLoader {
    private LoginService mScanService;

    public OrderLoginLoader() {
        mScanService = RetrofitServiceManager.getInstance().create(LoginService.class);
    }

    public Observable<LoginReponseBean> getLoginInFo(HashMap<String,String> map) {
        return observe(mScanService.loginIn(map)).map(new PayLoad<LoginReponseBean>());
    }

    public interface LoginService {
        @POST(Constant.registerLogin)
        Observable<BaseResponse<LoginReponseBean>> loginIn(@Body HashMap<String, String> map);
    }
}
