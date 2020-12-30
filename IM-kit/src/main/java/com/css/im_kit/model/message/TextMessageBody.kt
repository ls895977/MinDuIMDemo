package com.css.im_kit.model.message

/**
 * 文字消息消息体
 */
class TextMessageBody : BaseMessageBody {
    var text: String? = null

    constructor() : super()
    constructor(text: String?) : super() {
        this.text = text
    }

    constructor(isRead: Boolean, receivedTime: String?, sendTime: String?, sendAccount: String?, receiveAccount: String, text: String?) : super(isRead, receivedTime, sendTime, sendAccount, receiveAccount) {
        this.text = text
    }


}