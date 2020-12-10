package com.css.im_kit.db.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 会话
 */
@Entity
data class Conversation(
        var conversationId: String,//会话id
        var sendUserId: String,//发送方id
        var receiveUserId: String//接收方id
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    override fun toString(): String {
        return super.toString()
    }
}

