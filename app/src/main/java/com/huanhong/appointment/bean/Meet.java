package com.huanhong.appointment.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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
   private String gmtEnd,gmtStart;
   public int state,typeId,id;
   public String name,creatorName,peopleNum,delayTimeStr,deptName;

   public long getGmtEnd() {
      if(gmtEnd!=null){
         return getTime(gmtEnd);
      }
      return 0;
   }

   public long getGmtStart() {
      if(gmtStart!=null){
         return getTime(gmtStart);
      }
      return 0;
   }

   private long getTime(String date){
      SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      try {
         return fmt.parse(date).getTime();
      } catch (ParseException e) {
         e.printStackTrace();
      }
      return 0;
   }
}
