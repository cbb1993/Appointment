package com.huanhong.appointment.bean;

import com.huanhong.appointment.utils.SharedPreferencesUtils;

/**
 * Created by dhy
 * Date: 2019/5/9
 * Time: 11:19
 * describe:
 */
public class LoginReponseBean {

    /**
     * accessToken : eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxODEwMTY3MTk5MCIsImlhdCI6MTUzOTU3NDY1MSwic3ViIjoid3d3LmlkbS5jb20iLCJpc3MiOiJ3d3cuYXBwLmNvbSIsImV4cCI6MTUzOTU3NjQ1MX0.RA2AAJB2tDdpKPaVw38WBUvLaJehiE89Njv60QOw42U
     * refreshToken : eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxODEwMTY3MTk5MCIsImlhdCI6MTUzOTU3NDY1MSwic3ViIjoid3d3LmlkbS5jb20iLCJpc3MiOiJ3d3cuYXBwLmNvbSIsImV4cCI6MTUzOTU3NjQ1MX0.RA2AAJB2tDdpKPaVw38WBUvLaJehiE89Njv60QOw42U
     */

    public String token;

    public static void setToken(String token){
        SharedPreferencesUtils.addData("token","Bearer "+token);
    }
    public static String getToken(){
       return SharedPreferencesUtils.readData("token");
    }

    private Admin user;

    public Admin getUser() {
        return user;
    }

    public static class Admin{
        private String id,name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
        /*
        * "mchId":8,
    			"roleId":9,
    			"sex":1,
    			"companyName":"宏碁技术有钱公司",
    			"profilePic":"https://img.aiairy.com/default/profile_pic.png?x-oss-process=image/format,jpg/interlace,1",
    			"mobile":"18621717406",
    			"storeId":9,
    			"merchantState":1,
    			"setting":"",
    			"balance":0.00,
    			"jobNo":1001,
    			"name":"宏碁管理员",
    			"roleName":"超级管理员",
    			"position":"管理员",
    			"power":"",
    			"account":"100001",
    			"email":"root@liudeyi.cn"*/
    }
}
