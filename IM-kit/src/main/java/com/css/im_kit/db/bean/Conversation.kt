package com.css.im_kit.db.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 会话
 */
@Entity(tableName = "conversation")
data class Conversation(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "send_user_id") val sendUserId: String,//发送方id
        @ColumnInfo(name = "receive_user_id") val receiveUserId: String//接收方id
)
