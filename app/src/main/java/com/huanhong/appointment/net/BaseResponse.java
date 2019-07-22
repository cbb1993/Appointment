package com.huanhong.appointment.net;

/**
 * author dhy
 * Created by test on 2018/6/12.
 * 网络请求结果 基类
 */
public class BaseResponse<T> {
    public int status;
    public String message;
    public T data;

    public boolean isSuccess() {
        return status == 200;
    }
}