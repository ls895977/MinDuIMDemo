package com.css.im_kit.message

open class BaseMessageBody {
    /**
     * 是否新消息
     */
    var isRead: Boolean = false

    /**
     * 消息接收时间
     */
    var receivedTime: String = ""

    /**
     * 是否是自己发送的消息
     */
    var isSelf: Boolean = false
}