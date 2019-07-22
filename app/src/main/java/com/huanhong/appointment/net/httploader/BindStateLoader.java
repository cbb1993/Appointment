package com.huanhong.appointment.net.httploader;

import com.huanhong.appointment.constant.Constant;
import com.huanhong.appointment.bean.LoginReponseBean;
import com.huanhong.appointment.net.BaseResponse;
import com.huanhong.appointment.net.ObjectLoader;
import com.huanhong.appointment.net.PayLoad;
import com.huanhong.appointment.net.RetrofitServiceManager;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by dhy
 * Date: 2019/5/9
 * Time: 11:18
 * describe:
 */
public class BindStateLoader extends ObjectLoader {
    private LoginService mScanService;

    public BindStateLoader() {
        mScanService = RetrofitServiceManager.getInstance().create(LoginService.class);
    }

    public Observable<Object> getBindState(Map<String,String> map) {
        return observe(mScanService.getBindState(LoginReponseBean.getToken(),map)).map(new PayLoad<Object>());
    }

    public interface LoginService {
        @POST(Constant.BIND_STATE)
        Observable<BaseResponse<Object>> getBindState(@Header("Authorization") String token, @QueryMap Map<String,String> map);
    }
}
