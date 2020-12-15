package com.css.im_kit.model.conversation

import com.css.im_kit.model.message.SGMessage
import java.io.Serializable

/**
 * 会话列表
 *
 */
class SGConversation : Serializable {
    /**
     * 会话列表id
     */
    var id: String? = null

    /**
     * 店铺id
     */
    var shop_id: String? = null

    /**
     * 自己的聊天账号
     */
    var account: String? = null

    /**
     *对方聊天账号
     */
    var chat_account: String? = null

    /**
     * 最新消息
     */
    var newMessage: SGMessage? = null

    /**
     * 新消息数量
     */
    var unread_account: Int = 0

    /**
     * 店铺
     */
    var shop: Shop? = null

    constructor()
    constructor(id: String, shop_id: String, account: String, chat_account: String, newMessage: SGMessage, unread_account: Int, shop: Shop) {
        this.id = id
        this.shop_id = shop_id
        this.account = account
        this.chat_account = chat_account
        this.newMessage = newMessage
        this.unread_account = unread_account
        this.shop = shop
    }

}

class Shop(
        var shop_id: String,
        var shop_name: String,
        var log: String
) : Serializable