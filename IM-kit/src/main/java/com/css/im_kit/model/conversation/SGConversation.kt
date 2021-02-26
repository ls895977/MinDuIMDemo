package com.css.im_kit.model.conversation

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.model.userinfo.SGUserInfo
import java.io.Serializable

/**
 * 会话列表
 *
id	number
非必须
account	string
非必须
chat_account	string
非必须
shop_id	number
非必须
message_type	number
非必须
created_time	string
非必须
updated_time	string
非必须
data_state	string
非必须
content	string
非必须
聊天内容
unread_account	number
非必须
chat_account_info	object
非必须
聊天人的信息
 *
 */
class SGConversation : Serializable, MultiItemEntity {
    /**
     * 会话列表id
     */
    var id: String? = null

    /**
     * 自己的聊天账号
     */
    var account: String? = null

    /**
     *对方聊天账号
     */
    var chat_account: String? = null

    /**
     * 店铺id
     */
    var shop_id: String? = null

    /**
     * 最新消息
     */
    var newMessage: SGMessage? = null

    /**
     *置顶字段，0 是不置顶
     */
    var sort: Int = 0

    /**
     * 新消息数量
     */
    var unread_account: Int = 0

    /**
     * 用户
     */
    var chat_account_info: SGUserInfo? = null

    /**
     * 店铺信息
     */
    var shop: Shop? = null

    constructor()
    constructor(id: String?, account: String?, chat_account: String?, shop_id: String?, newMessage: SGMessage?, unread_account: Int, sort: Int, chat_account_info: SGUserInfo?, shop: Shop?) {
        this.id = id
        this.account = account
        this.chat_account = chat_account
        this.shop_id = shop_id
        this.newMessage = newMessage
        this.unread_account = unread_account
        this.chat_account_info = chat_account_info
        this.shop = shop
        this.sort = sort
    }

    override fun getItemType(): Int {
        return 1
    }
}

class Shop(
        var shop_id: String,
        var shop_name: String,
        var self_shop: String,
        var brand_name: String,
        var log: String
) : Serializable