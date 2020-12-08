package com.css.im_kit.db.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户资料
 */
@Entity(tableName = "user_info")
data class UserInfo(
        @PrimaryKey val userId: Int,
        @ColumnInfo(name = "nick_name") val nickName: String,
        @ColumnInfo(name = "avatar") val avatar: String
)
