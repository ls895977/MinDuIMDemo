package com.css.im_kit.db.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.css.im_kit.imservice.bean.SendMessageBean

/**
 * 消息
 */
@Entity
data class Message(
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
) {
    @PrimaryKey(autoGenerate = true)
    var targetId = 0//id

    /**
     * 转换为service发送消息数据类型
    String content;
    String chat_id;
    String receive_id;
    String send_id;
    String type;
     */
    fun toSendMessageBean(): SendMessageBean {
        return SendMessageBean(this.content, this.conversationId, this.receiveUserId, this.sendUserId, this.type)
    }
}

enum class SendType(var text: Int) {
    SENDING(0),
    SUCCESS(1),
    FAIL(2)
}