package com.css.im_kit.db.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户资料
 */
@Entity
data class UserInfo(
        var account: String,
        var user_type: String,
        var nickname: String,
        var avatar: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}