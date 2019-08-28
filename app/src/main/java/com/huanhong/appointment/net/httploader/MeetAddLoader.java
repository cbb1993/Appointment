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
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by dhy
 * Date: 2019/5/9
 * Time: 11:18
 * describe:
 */
public class MeetAddLoader extends ObjectLoader {
    private RequestService mScanService;

    public MeetAddLoader() {
        mScanService = RetrofitServiceManager.getInstance().create(RequestService.class);
    }

    public Observable<Object> request(String token,HashMap<String,Object> map) {
        return observe(mScanService.request("Bearer_"+token,map)).map(new PayLoad<Object>());
    }

    public interface RequestService {
        @POST(Constant.MEET_ADD)
        Observable<BaseResponse<Object>> request(@Header("Authorization") String token, @Body HashMap<String, Object> map);
    }
    /*
    * {
    "name":"测试会议",
    "gmtStart":"2019-07-16 18:20:00",
    "gmtEnd":"2019-07-16 18:20:00",
    "roomId":137,
    "roomName":"会议室 01",
    "meetingUsers":[
        {
            "attendeeId":140,
            "attendeeName":"元"
        }
    ]
}
    * */
}
