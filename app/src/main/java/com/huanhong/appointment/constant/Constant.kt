package com.huanhong.appointment.constant


object Constant {
    const val HAS_NETWORK_KEY = "has_network"
    const val Base = "https://vis.bjcapitalland.com.cn/"
//    const val Base = "https://www.aiairy.com/"
    const val BASE_URL = "${Base}ryzf-platform/api/"
    const val BASE_URL_C = "${Base}ryzf-consumer/api/"

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
    const val version =  "meeting/version"
    // 会议签到
    const val signIn =  "meeting/signIn"
    // 会议类型
    const val type =  "meetingType/info/{id}"

    /*------------c端接口---------------*/

    const val registerLogin =BASE_URL_C + "user/registerLogin"
    const val getSmsCode =BASE_URL_C + "user/getSmsCode"
    const val meetingUsers =BASE_URL_C + "meeting/meetingUsers"
    const val MEET_ADD =BASE_URL_C + "meeting/add"


    const val QR_CODE = BASE_URL+"roomInventory/qr"
    const val equipments = BASE_URL + "equipment/room/{id}"
    const val station = BASE_URL + "station/room/{id}"

    //上传人脸图片

    const val compareFace1N = "${Base}visitor/api/sense/compareFace1N"
}
