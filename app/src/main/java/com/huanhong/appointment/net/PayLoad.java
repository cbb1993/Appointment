package com.huanhong.appointment.net;


import android.util.Log;

import io.reactivex.functions.Function;

/**
 * author dhy
 * Created by test on 2018/6/12.
 * 剥离 最终数据
 */
public class PayLoad<T> implements Function<BaseResponse<T>, T> {

    @Override
    public T apply(BaseResponse<T> tBaseResponse) throws Exception {
        if (!tBaseResponse.isSuccess()) {
            Log.e("-111------","-----");
            throw new Fault(tBaseResponse.status, tBaseResponse.message);
        }
        Log.e("-2222222222------","-----");
        if(tBaseResponse.data == null ){
            tBaseResponse.data = (T) "";
        }
        Log.e("-33333------","-----");
        return tBaseResponse.data;
    }
}