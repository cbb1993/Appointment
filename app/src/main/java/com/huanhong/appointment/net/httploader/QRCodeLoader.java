package com.huanhong.appointment.net.httploader;

import com.huanhong.appointment.bean.LoginReponseBean;
import com.huanhong.appointment.bean.MeetDevice;
import com.huanhong.appointment.constant.Constant;
import com.huanhong.appointment.net.BaseResponse;
import com.huanhong.appointment.net.ObjectLoader;
import com.huanhong.appointment.net.PayLoad;
import com.huanhong.appointment.net.RetrofitServiceManager;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.QueryMap;

/**
 * Created by dhy
 * Date: 2019/5/9
 * Time: 11:18
 * describe:
 */
public class QRCodeLoader extends ObjectLoader {
    private RequestService mScanService;

    public QRCodeLoader() {
        mScanService = RetrofitServiceManager.getInstance().create(RequestService.class);
    }

    public Observable<String> request(Map<String,Object> map) {
        return observe(mScanService.request(LoginReponseBean.getToken(),map)).map(new PayLoad<String>());
    }

    public interface RequestService {
        @GET(Constant.QR_CODE)
        Observable<BaseResponse<String>> request(@Header("Authorization") String token, @QueryMap Map<String, Object> map);
    }
}
