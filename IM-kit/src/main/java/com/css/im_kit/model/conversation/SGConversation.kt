package com.css.im_kit.model.conversation

import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.model.userinfo.SGUserInfo

/**
 * 会话列表
 *
 */
class SGConversation {
    /**
     * 会话列表id
     */
    var conversationId: String? = null

    /**
     * 用户信息
     */
    var userInfo: SGUserInfo? = null

    /**
     * 最新消息
     */
    var newMessage: SGMessage? = null

    /**
     * 新消息数量
     */
    var newsNum: Int = 0

    constructor()

    constructor(conversationId: String?, userInfo: SGUserInfo?, newMessage: SGMessage?, newsNum: Int) {
        this.conversationId = conversationId
        this.userInfo = userInfo
        this.newMessage = newMessage
        this.newsNum = newsNum
    }

}