package com.huanhong.appointment.bean;

import java.util.List;

/**
 * Created by 坎坎.
 * Date: 2019/7/29
 * Time: 13:51
 * describe:
 */
public class Staff {
    String name ,deptId;
    public List<User> users;

    public static class User{
        public String userId ,userName ,userTel ,deptId;
        public boolean check;
    }

}
