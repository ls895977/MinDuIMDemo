package com.css.im_kit.imservice.bean

import com.css.im_kit.db.gson
import java.io.Serializable

class SendMessageBean(
        var m_id: String,
        var type: Int,
        var source: Int,
        var receive_account: String,
        var send_account: String,
        var content: String,
        var time: Long,
        var extend: HashMap<String,String>

) : Serializable {
    fun toJsonString(): String {
        return gson.toJson(this)
    }

    fun extendToJsonString(): String {
        return gson.toJson(extend)
    }
}