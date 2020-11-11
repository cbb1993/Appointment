package com.huanhong.appointment.constant;


import com.huanhong.appointment.utils.SharedPreferencesUtils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BaseUrlInterceptor implements Interceptor {

   public static void setBaseUrl(String host){
       SharedPreferencesUtils.addData("host",host);
   }
    public static String readBaseUrl(){
        String host = SharedPreferencesUtils.readData("host", "");
        if("".equals(host)){
            host = Constant.Base;
        }
        return host;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        //获取request
        Request request = chain.request();
        //从request中获取原有的HttpUrl实例oldHttpUrl
        HttpUrl oldHttpUrl = request.url();
        //获取request的创建者builder
        Request.Builder builder = request.newBuilder();
        //从request中获取headers，通过给定的键url_name
        //如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
        builder.removeHeader("urlname");
        //匹配获得新的BaseUrl

        HttpUrl newBaseUrl = null;
        newBaseUrl = HttpUrl.parse(readBaseUrl());

        //重建新的HttpUrl，修改需要修改的url部分
        HttpUrl newFullUrl = oldHttpUrl
                .newBuilder()
                .scheme(newBaseUrl.scheme())
                .host(newBaseUrl.host())//更换主机名
                .port(newBaseUrl.port())//更换端口
                .build();
        //重建这个request，通过builder.url(newFullUrl).build()；
        // 然后返回一个response至此结束修改
        return chain.proceed(builder.url(newFullUrl).build());


    }
}
