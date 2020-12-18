package com.css.im_kit.http.bean

import com.css.im_kit.model.conversation.Shop

data class AssignCustomerBack(
        var id: String,
        var account: String,
        var user_type: String,
        val nickname: String,
        var avatar: String,
        var shop_id: String,
        var chat_shop: Shop? = null
)