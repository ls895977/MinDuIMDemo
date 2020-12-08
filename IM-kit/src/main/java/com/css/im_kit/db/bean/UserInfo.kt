package com.css.im_kit.db.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户资料
 */
@Entity
data class User_Info(
        var nickName: String,
        var avatar: String
) {
    @PrimaryKey(autoGenerate = true)
    var userId: Int = 0


    override fun toString(): String {
        return nickName
    }
}