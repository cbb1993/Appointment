package com.huanhong.appointment.net;

import android.app.Dialog;
import android.util.Log;


import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

/**
 * author dhy
 * Created by test on 2018/7/2.
 */

public class ThrowableUtils {
    public static void ThrowableEnd( Throwable throwable, Dialog dialog) {
        if (dialog != null)
            dialog.dismiss();
        //出现错误
        if (throwable instanceof HttpException) { // 网络错误
            DialogUtils.toastShow("网络异常，请稍后重试");
        } else if (throwable instanceof SocketTimeoutException) { // 其他错误
            DialogUtils.toastShow("网络请求超时，请稍后重试");
        } else if (throwable instanceof ConnectException) {
            DialogUtils.toastShow("无网络，请求失败");
        } else if (throwable instanceof Fault) {
            DialogUtils.toastShow(((Fault) throwable).message);
        } else if (throwable instanceof NullPointerException) {
            DialogUtils.toastShow("请求数据为空");
        } else if (throwable instanceof UnknownHostException) {
            DialogUtils.toastShow("无网络连接，请求失败");
        } else {
            DialogUtils.toastShow(throwable.getMessage());
        }
    }

    public static String ThrowableEn3( Throwable throwable) {
        //出现错误
        if (throwable instanceof HttpException) { // 网络错误
            return "网络异常，请稍后重试";
        } else if (throwable instanceof SocketTimeoutException) { // 其他错误
            return "网络请求超时，请稍后重试";
        } else if (throwable instanceof ConnectException) {
            return "无网络，请求失败";
        } else if (throwable instanceof Fault) {
            return ((Fault) throwable).message;
        } else if (throwable instanceof NullPointerException) {
            return "请求数据为空";
        } else if (throwable instanceof UnknownHostException) {
            return "无网络连接，请求失败";
        } else {
            return throwable.getMessage();
        }
    }

    public static String ThrowableEnd2( Throwable throwable) {
        //出现错误
        if (throwable instanceof HttpException) { // 网络错误
            return "网络异常，请稍后重试";
        } else if (throwable instanceof SocketTimeoutException) { // 其他错误
            return "网络请求超时，请稍后重试";
        } else if (throwable instanceof ConnectException) {
            return "无网络，请求失败";
        } else if (throwable instanceof Fault) {
            return ((Fault) throwable).message;
        } else if (throwable instanceof NullPointerException) {
            return "请求数据为空";
        } else {
            return "签到失败，请重新签到";
        }
    }
}
