package com.css.im_kit.ui.bean


class MessageContent {
    private var messageId = ""
    private var messageText = ""
    private var messageImage = ""
    private var messageIcon = ""

    constructor()
    constructor(messageId: String, messageText: String, messageImage: String, messageIcon: String) {
        this.messageId = messageId
        this.messageText = messageText
        this.messageImage = messageImage
        this.messageIcon = messageIcon
    }
}