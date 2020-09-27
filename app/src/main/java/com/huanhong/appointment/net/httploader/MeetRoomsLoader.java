package com.huanhong.appointment.net.httploader;

import com.huanhong.appointment.bean.LoginReponseBean;
import com.huanhong.appointment.bean.Room;
import com.huanhong.appointment.constant.Constant;
import com.huanhong.appointment.net.BaseResponse;
import com.huanhong.appointment.net.ObjectLoader;
import com.huanhong.appointment.net.PayLoad;
import com.huanhong.appointment.net.RetrofitServiceManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class MeetRoomsLoader extends ObjectLoader {
    private RequestService mScanService;

    public MeetRoomsLoader() {
        mScanService = RetrofitServiceManager.getInstance().create(RequestService.class);
    }

    public Observable<List<Room>> getRooms(HashMap<String, String> map) {
        return observe(mScanService.getRooms(LoginReponseBean.getToken(),map)).map(new PayLoad<List<Room>>());
    }

    public interface RequestService {
        @GET(Constant.ROOMS)
        Observable<BaseResponse<List<Room>>> getRooms(@Header("Authorization") String token,@QueryMap HashMap<String, String> map);
    }
}
