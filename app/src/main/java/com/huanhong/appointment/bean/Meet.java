package com.huanhong.appointment.bean;

/**
 * Created by 坎坎.
 * Date: 2019/7/19
 * Time: 11:30
 * describe:
 */
public class Meet {
   /*
   * {
            "gmtEnd":1559112330000,
            "gmtStart":1559112330000,
            "id":3,
            "name":"周会"
        },*/
   public long gmtEnd,gmtStart;
   public int state;
   public String id,name,creatorName,peopleNum,delayTimeStr;
}
