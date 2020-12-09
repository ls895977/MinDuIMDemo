package com.css.im_kit.conversation

import com.css.im_kit.message.SGMessage
import com.css.im_kit.userinfo.SGUserInfo

/**
 * 会话列表
 *
 */
class SGConversation {
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
}