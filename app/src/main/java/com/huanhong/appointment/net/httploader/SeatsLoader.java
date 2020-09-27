package com.huanhong.appointment.net.httploader;

import com.huanhong.appointment.bean.Equipment;
import com.huanhong.appointment.bean.LoginReponseBean;
import com.huanhong.appointment.bean.Seat;
import com.huanhong.appointment.constant.Constant;
import com.huanhong.appointment.net.BaseResponse;
import com.huanhong.appointment.net.ObjectLoader;
import com.huanhong.appointment.net.PayLoad;
import com.huanhong.appointment.net.RetrofitServiceManager;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by dhy
 * Date: 2019/5/9
 * Time: 11:18
 * describe:
 */
public class SeatsLoader extends ObjectLoader {
    private RequestService mScanService;

    public SeatsLoader() {
        mScanService = RetrofitServiceManager.getInstance().create(RequestService.class);
    }

    public Observable<List<Seat>> request(String id) {
        return observe(mScanService.request(LoginReponseBean.getToken(),id)).map(new PayLoad<List<Seat>>());
    }

    public interface RequestService {
        @GET(Constant.station)
        Observable<BaseResponse<List<Seat>>> request(@Header("Authorization") String token, @Path("id") String id);
    }
}
