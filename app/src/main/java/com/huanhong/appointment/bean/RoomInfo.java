package com.huanhong.appointment.bean;

import java.util.List;

/**
 * Created by 坎坎.
 * Date: 2019/9/5
 * Time: 13:13
 * describe:
 */
public class RoomInfo {
    public Configuration configuration;
    public List<Meet> list;
    /*
    {

    			"delayMinute":0,

    			"delayType":1,

    			"deviceCount":1,

    			"maxUseTime":120,

    			"mchId":8,

    			"needAudit":2,

    			"roomId":257,

    			"roomPeopleCount":12,

    			"signInType":3,

    			"storeId":9

    		}*/

    public static class Configuration{
        public int delayMinute,delayType,
                deviceCount,maxUseTime,mchId,
                needAudit,roomId,roomPeopleCount,
                signInType,storeId;
    }
}
