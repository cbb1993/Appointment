package com.huanhong.appointment.net.httploader;

import com.huanhong.appointment.bean.LoginReponseBean;
import com.huanhong.appointment.bean.Staff;
import com.huanhong.appointment.constant.Constant;
import com.huanhong.appointment.net.BaseResponse;
import com.huanhong.appointment.net.ObjectLoader;
import com.huanhong.appointment.net.PayLoad;
import com.huanhong.appointment.net.RetrofitServiceManager;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by dhy
 * Date: 2019/5/9
 * Time: 11:18
 * describe:
 */
public class MeetingUsersLoader extends ObjectLoader {
    private RequestService mScanService;

    public MeetingUsersLoader() {
        mScanService = RetrofitServiceManager.getInstance().create(RequestService.class);
    }

    public Observable<List<Staff>> getMeetingUsers(String token) {
        return observe(mScanService.getMeetingUsers(token)).map(new PayLoad<List<Staff>>());
    }

    public interface RequestService {
        @GET(Constant.meetingUsers)
        Observable<BaseResponse<List<Staff>>> getMeetingUsers(@Header("Authorization") String token);
    }
}
