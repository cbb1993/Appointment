package com.huanhong.appointment.net.httploader;

import com.huanhong.appointment.bean.AppVersion;
import com.huanhong.appointment.bean.LoginReponseBean;
import com.huanhong.appointment.constant.Constant;
import com.huanhong.appointment.net.BaseResponse;
import com.huanhong.appointment.net.ObjectLoader;
import com.huanhong.appointment.net.PayLoad;
import com.huanhong.appointment.net.RetrofitServiceManager;

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
public class UpdateLoader extends ObjectLoader {
    private RequestService mScanService;

    public UpdateLoader() {
        mScanService = RetrofitServiceManager.getInstance().create(RequestService.class);
    }

    public Observable<AppVersion> getVersion() {
        return observe(mScanService.getVersion(LoginReponseBean.getToken())).map(new PayLoad<AppVersion>());
    }

    public interface RequestService {
        @GET(Constant.version)
        Observable<BaseResponse<AppVersion>> getVersion(@Header("Authorization") String token);
    }
}
