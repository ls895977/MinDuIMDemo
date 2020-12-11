package com.css.im_kit.db.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户资料
 */
@Entity
data class UserInfo(
        var nickName: String,
        var avatar: String,
        var userId: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0


    override fun toString(): String {
        return nickName
    }
}