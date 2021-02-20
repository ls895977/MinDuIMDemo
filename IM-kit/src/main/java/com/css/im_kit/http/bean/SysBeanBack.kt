package com.css.im_kit.http.bean

import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.Serializable

data class SysBeanBack(
        var content: String,
        var sys_type: Int,
        var created_time: String,
        var unread_number: Int
) : Serializable, MultiItemEntity {
    override fun getItemType(): Int {
        return 2
    }
}