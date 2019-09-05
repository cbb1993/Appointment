package com.huanhong.appointment.net.httploader;

import com.huanhong.appointment.bean.LoginReponseBean;
import com.huanhong.appointment.constant.Constant;
import com.huanhong.appointment.net.BaseResponse;
import com.huanhong.appointment.net.ObjectLoader;
import com.huanhong.appointment.net.PayLoad;
import com.huanhong.appointment.net.RetrofitServiceManager;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;

/**
 * Created by dhy
 * Date: 2019/5/9
 * Time: 11:18
 * describe:
 */
public class DelayMeetMeetRoomsLoader extends ObjectLoader {
    private RequestService mScanService;

    public DelayMeetMeetRoomsLoader() {
        mScanService = RetrofitServiceManager.getInstance().create(RequestService.class);
    }

    public Observable<Object> request(Map<String,Object> map) {
        return observe(mScanService.request(LoginReponseBean.getToken(),map)).map(new PayLoad<Object>());
    }

    public interface RequestService {
        @PUT(Constant.DELAY_MEET)
        Observable<BaseResponse<Object>> request(@Header("Authorization") String token, @Body Map<String, Object> map);
    }
}
