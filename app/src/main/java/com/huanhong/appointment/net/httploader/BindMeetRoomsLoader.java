package com.huanhong.appointment.net.httploader;

import com.huanhong.appointment.bean.LoginReponseBean;
import com.huanhong.appointment.bean.Room;
import com.huanhong.appointment.constant.Constant;
import com.huanhong.appointment.net.BaseResponse;
import com.huanhong.appointment.net.ObjectLoader;
import com.huanhong.appointment.net.PayLoad;
import com.huanhong.appointment.net.RetrofitServiceManager;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by dhy
 * Date: 2019/5/9
 * Time: 11:18
 * describe:
 */
public class BindMeetRoomsLoader extends ObjectLoader {
    private RequestService mScanService;

    public BindMeetRoomsLoader() {
        mScanService = RetrofitServiceManager.getInstance().create(RequestService.class);
    }

    public Observable<Object> bind(Map<String,Object> map) {
        return observe(mScanService.bind(LoginReponseBean.getToken(),map)).map(new PayLoad<Object>());
    }

    public interface RequestService {
        @POST(Constant.BIND)
        Observable<BaseResponse<Object>> bind(@Header("Authorization") String token, @Body Map<String,Object> map);
    }
}
