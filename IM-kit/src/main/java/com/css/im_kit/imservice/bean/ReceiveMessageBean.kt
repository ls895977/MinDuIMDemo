package com.css.im_kit.imservice.bean

import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.bean.SendType

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
        var type: String,
        var content: String,
        var receive_id: String,
        var send_id: String,
        var time: Long,
        var extra: String,
        var code: Int = 0
) {

    /**
    var conversationId: String,//聊天室id
    var messageId: String,//消息id
    var sendTime: Long,//发送时间
    var receivedTime: Long,//接收时间
    var content: String,//内容
    var sendUserId: String,//发送方id
    var receiveUserId: String,//接收方id
    var type: String,
    var sendType: Int = 0,//是否发送成功
    var isRead: Boolean//是否已读
     */
    fun toDBMessage(): Message {
        return Message(
                conversationId = "",
                messageId = m_id,
                sendTime = time,
                receivedTime = System.currentTimeMillis(),
                content = content,
                sendUserId = send_id,
                receiveUserId = receive_id,
                type = type,
                sendType = SendType.SUCCESS.text,
                isRead = false
        )
    }
}