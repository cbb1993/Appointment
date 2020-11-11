package com.huanhong.appointment.utils;

import com.huanhong.appointment.BaseApplication;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
    public static void appendMethodB(String content) {
        File dir = new File(BaseApplication.path);
        File meetingLogs = new File(dir,"meetingLogs.txt");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!meetingLogs.exists()) {
            try {
                meetingLogs.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(meetingLogs, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendMessage(String content) {
        File dir = new File(BaseApplication.path);
        File meetingLogs = new File(dir,"request.txt");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!meetingLogs.exists()) {
            try {
                meetingLogs.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter writer = new FileWriter(meetingLogs, true);
            writer.write(content+"\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
