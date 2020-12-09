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

    constructor(isRead: Boolean?, receivedTime: String?, sendTime: String?, isSelf: Boolean?, text: String?) : super(isRead, receivedTime, sendTime, isSelf) {
        this.text = text
    }


}