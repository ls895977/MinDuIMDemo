package com.css.im_kit.message

class TextMessageBody : BaseMessageBody {
    var text: String? = null

    constructor() : super()
    constructor(text: String?) : super() {
        this.text = text
    }

}