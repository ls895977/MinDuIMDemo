package com.css.im_kit.imservice.bean

import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.bean.SendType
import com.css.im_kit.db.gson
import com.css.im_kit.model.message.MessageType

data class ReceiveMessageBean(
        /**
         * m_id : 160768300748
         * type : 0
         * content : 2234
         * receive_id : 2
         * send_id : 1
         * time : 1607683007
         * extra :
         * code : 2000
         */
        var m_id: String,
        var shop_id: String,
        var type: Int,
        var content: String,
        var receive_account: String,
        var send_account: String,
        var time: Long,
        var extend: String?,
        var code: Int = 0
) {

    /**
    var message_id: String,//消息id
    var send_account: String,//发送账号
    var receive_account: String,//接收账号
    var shop_id: String,//店铺id
    var message_type: String,//消息类型
    var read_status: Boolean,//是否未读
    var send_status: Int,//是否发送成功
    var send_time: Long,//发送时间
    var receive_time: Long,//接收时间
    var message: String//消息内容
    var extend: String? = ""//扩展消息
     */
    fun toDBMessage(): Message {
        val type: String = when (type) {
            1 -> MessageType.TEXT.str
            3 -> MessageType.IMAGE.str
            2 -> MessageType.COMMODITY.str
            else -> MessageType.TEXT.str
        }
        val message = Message(
                message_id = m_id,
                send_account = send_account,
                receive_account = receive_account,
                shop_id = shop_id,
                message_type = type,
                read_status = false,
                send_status = SendType.SUCCESS.text,
                send_time = time,
                receive_time = System.currentTimeMillis(),
                message = content
        )
        message.extend = extend
        return message
    }

    fun toJsonString(): String {
        return gson.toJson(this)
    }
}