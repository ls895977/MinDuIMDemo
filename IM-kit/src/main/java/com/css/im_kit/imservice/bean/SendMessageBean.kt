package com.css.im_kit.imservice.bean

import com.css.im_kit.db.gson

class SendMessageBean(
        var content: String,
        var chat_id: String,
        var receive_id: String,
        var send_id: String,
        var type: String
) {
    fun toJsonString(): String {
        return gson.toJson(this)
    }
}