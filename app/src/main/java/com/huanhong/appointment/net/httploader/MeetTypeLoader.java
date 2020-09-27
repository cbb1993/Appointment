package com.huanhong.appointment.net.httploader;

import com.huanhong.appointment.bean.LoginReponseBean;
import com.huanhong.appointment.bean.MeetingType;
import com.huanhong.appointment.constant.Constant;
import com.huanhong.appointment.net.BaseResponse;
import com.huanhong.appointment.net.ObjectLoader;
import com.huanhong.appointment.net.PayLoad;
import com.huanhong.appointment.net.RetrofitServiceManager;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by dhy
 * Date: 2019/5/9
 * Time: 11:18
 * describe:
 */
public class MeetTypeLoader extends ObjectLoader {
    private RequestService mScanService;

    public MeetTypeLoader() {
        mScanService = RetrofitServiceManager.getInstance().create(RequestService.class);
    }

    public Observable<MeetingType> request(String id) {
        return observe(mScanService.request(LoginReponseBean.getToken(),id).map(new PayLoad<MeetingType>()));
    }

    public interface RequestService {
        @GET(Constant.type)
        Observable<BaseResponse<MeetingType>> request(@Header("Authorization") String token, @Path("id") String id);
    }
}
