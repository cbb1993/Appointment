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
            throw new Fault(tBaseResponse.status, tBaseResponse.message);
        }
        if(tBaseResponse.data == null ){
            tBaseResponse.data = (T) "";
        }
        return tBaseResponse.data;
    }
}