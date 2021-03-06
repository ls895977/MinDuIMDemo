package com.css.im_kit.model.message

/**
 * 图片类型消息体
 */
class ImageMessageBody : BaseMessageBody {
    var imageUrl: String? = null


    constructor() : super()
    constructor(imageUrl: String?) : super() {
        this.imageUrl = imageUrl
    }

    constructor(isRead: Boolean, receivedTime: String?, sendTime: String?, sendAccount: String?, receiveAccount: String, imageUrl: String?) : super(isRead, receivedTime, sendTime, sendAccount, receiveAccount) {
        this.imageUrl = imageUrl
    }


}