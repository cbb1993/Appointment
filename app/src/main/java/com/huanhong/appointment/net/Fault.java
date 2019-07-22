package com.huanhong.appointment.net;

/**
 * author dhy
 * Created by test on 2018/6/12.
 */

public class Fault extends RuntimeException {
    public int status;
    public String message;

    public Fault(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
