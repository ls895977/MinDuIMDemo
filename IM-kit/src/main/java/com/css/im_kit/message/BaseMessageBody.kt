package com.css.im_kit.message

open class BaseMessageBody {
    /**
     * 是否新消息
     */
    var isRead: Boolean? = null

    /**
     * 消息接收时间
     */
    var receivedTime: String? = null

    /**
     * 消息发送时间
     */
    var sendTime: String? = null

    /**
     * 是否是自己发送的消息
     */
    var isSelf: Boolean? = null

    constructor()
    constructor(isRead: Boolean?, receivedTime: String?, sendTime: String?, isSelf: Boolean?) {
        this.isRead = isRead
        this.receivedTime = receivedTime
        this.sendTime = sendTime
        this.isSelf = isSelf
    }


}