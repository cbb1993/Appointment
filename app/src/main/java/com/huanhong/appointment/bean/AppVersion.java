package com.huanhong.appointment.bean;

/**
 * Created by 坎坎.
 * Date: 2019/10/21
 * Time: 17:52
 * describe:
 */
public class AppVersion {
    /*
    * {
        "resource":"{"md5": "", "path": "https://ryzf.oss-cn-shanghai.aliyuncs.com/apk/meeting/meeting_new.apk"}",
        "version":"1.0"
    }
    * */
    public String version;
    public String resource;

    public class Resource{
       public String md5 ,path;
    }
}
