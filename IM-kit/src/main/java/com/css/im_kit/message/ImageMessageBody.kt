package com.css.im_kit.message

/**
 * 图片类型消息体
 */
class ImageMessageBody : BaseMessageBody {
    var imageUrl: String? = null

    constructor() : super()
    constructor(imageUrl: String?) : super() {
        this.imageUrl = imageUrl
    }
}