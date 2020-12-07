package com.css.im_kit.db.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 消息
 */
@Entity(tableName = "message")
data class Message(
        @PrimaryKey val targetId: String? = null,//id
        @ColumnInfo(name = "message_id") val messageId: String,//消息id
        @ColumnInfo(name = "sent_time") val sentTime: String,//发送时间
        @ColumnInfo(name = "received_time") val receivedTime: String,//接收时间
        @ColumnInfo(name = "message_content") val content: String,//接收时间
        @ColumnInfo(name = "send_user_id") val sendUserId: String,//发送方id
        @ColumnInfo(name = "receive_user_id") val receiveUserId: String//接收方id
)