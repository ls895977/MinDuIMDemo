package com.css.im_kit.message

class ImageMessageBody : BaseMessageBody {
    var imageUrl: String? = null

    constructor() : super()
    constructor(imageUrl: String?) : super() {
        this.imageUrl = imageUrl
    }
}