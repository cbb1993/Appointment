package com.huanhong.appointment.constant


object Constant {
    const val HAS_NETWORK_KEY = "has_network"
    const val BASE_URL = "http://www.aiairy.com/ryzf-platform/api/"
//    const val BASE_URL = "http://192.168.1.20:8082/ryzf-platform/api/"
    const val BASE_URL_C = "https://www.aiairy.com/ryzf-consumer/api/"
//    const val BASE_URL_C = "https://192.168.1.20:8081/ryzf-consumer/api/"

    /*------------b端接口---------------*/
    const val SIGN_IN = "login"
    const val BIND_STATE = "meeting/verification"
    const val ROOMS = "meeting/room"
    const val BIND = "meeting/binding"
    const val ROOM_INFO = "meeting/room/info"
    const val UNBIND = "meeting/unBinding"
    const val END_MEET = "meeting/end"
    const val UPDATE_MEET = "meeting/update/state"
    const val DELAY_MEET =  "meeting/delay"
    const val MEET_DEVICES =  "meeting/devices"
    const val variable_list =  "variable/list"

    /*------------c端接口---------------*/

    const val registerLogin =BASE_URL_C + "user/registerLogin"
    const val getSmsCode =BASE_URL_C + "user/getSmsCode"
    const val meetingUsers =BASE_URL_C + "meeting/meetingUsers"
    const val MEET_ADD =BASE_URL_C + "meeting/add"
}
