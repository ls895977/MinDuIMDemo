package com.css.im_kit.db.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 消息
 */
@Entity
data class Message(
        @PrimaryKey
        var targetId: String,//id
        var messageId: String,//消息id
        var sendTime: String,//发送时间
        var receivedTime: String,//接收时间
        var content: String,//内容
        var sendUserId: String,//发送方id
        var receiveUserId: String//接收方id
)