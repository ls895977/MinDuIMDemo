package com.css.im_kit.db.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.css.im_kit.imservice.bean.ReceiveMessageBean
import com.css.im_kit.model.message.MessageType

/**
 * 消息
 */
@Entity
data class Message(
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

) {
    @PrimaryKey(autoGenerate = true)
    var id = 0//id
    var extend: String? = ""//扩展消息

    /**
     *
     *    var m_id: String,
    var shop_id: String,
    var type: Int,
    var content: String,
    var receive_account: String,
    var send_account: String,
    var time: Long,
    var extend: String?,
    var code: Int = 0
     */
    fun toSendMessageBean(): ReceiveMessageBean {
        val type: Int = when (message_type) {
            MessageType.TEXT.str -> 1
            MessageType.IMAGE.str -> 3
            MessageType.COMMODITY.str -> 2
            else -> 1
        }
        return ReceiveMessageBean(message_id, shop_id, type, message_id, receive_account, send_account, send_time, extend, 2000)
    }
}

enum class SendType(var text: Int) {
    SENDING(0),
    SUCCESS(1),
    FAIL(2)
}