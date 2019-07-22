package com.huanhong.appointment.net.httploader;

import com.huanhong.appointment.bean.LoginReponseBean;
import com.huanhong.appointment.bean.Meet;
import com.huanhong.appointment.bean.Room;
import com.huanhong.appointment.constant.Constant;
import com.huanhong.appointment.net.BaseResponse;
import com.huanhong.appointment.net.ObjectLoader;
import com.huanhong.appointment.net.PayLoad;
import com.huanhong.appointment.net.RetrofitServiceManager;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
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
public class RoomMeetsLoader extends ObjectLoader {
    private LoginService mScanService;

    public RoomMeetsLoader() {
        mScanService = RetrofitServiceManager.getInstance().create(LoginService.class);
    }

    public Observable<List<Meet>> getRoomMeets(HashMap<String,String> map) {
        return observe(mScanService.getRoomMeets(LoginReponseBean.getToken(),map)).map(new PayLoad<List<Meet>>());
    }

    public interface LoginService {
        @POST(Constant.ROOM_INFO)
        Observable<BaseResponse<List<Meet>>> getRoomMeets(@Header("Authorization") String token, @QueryMap HashMap<String,String> map);
    }
}
