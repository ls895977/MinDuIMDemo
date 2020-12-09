package com.css.im_kit.message

/**
 * 文字消息消息体
 */
class TextMessageBody : BaseMessageBody {
    var text: String? = null

    constructor() : super()
    constructor(text: String?) : super() {
        this.text = text
    }

}