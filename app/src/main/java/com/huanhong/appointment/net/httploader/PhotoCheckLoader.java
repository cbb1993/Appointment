package com.huanhong.appointment.net.httploader;

import com.huanhong.appointment.bean.CheckPhotoBean;
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
import kotlin.jvm.JvmSuppressWildcards;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/**
 * Created by dhy
 * Date: 2019/5/9
 * Time: 11:18
 * describe:
 */
public class PhotoCheckLoader extends ObjectLoader {
    private RequestService mScanService;

    public PhotoCheckLoader() {
        mScanService = RetrofitServiceManager.getInstance().create(RequestService.class);
    }

    public Observable<CheckPhotoBean> request(MultipartBody.Part file) {
        return observe(mScanService.request("1",file)).map(new PayLoad<CheckPhotoBean>());
    }

    public interface RequestService {
        // 192.168.1.23:8081
        @Multipart
        @POST(Constant.compareFace1N)
        Observable<BaseResponse<CheckPhotoBean>> request(@Query ("lib_ids") String id,
                                                 @Part MultipartBody.Part file  );
    }
}
