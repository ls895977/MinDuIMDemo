package com.css.im_kit.ui.bean

import com.chad.library.adapter.base.entity.MultiItemEntity

class MessageBean : MultiItemEntity {
    private var messageId = ""
    private var messageType: MessageType? = null
    private var messageContent: MessageContent? = null
    private var time = ""

    constructor()
    constructor(messageId: String, messageType: MessageType, messageContent: MessageContent, time: String) {
        this.messageId = messageId
        this.messageType = messageType
        this.messageContent = messageContent
        this.time = time
    }

    override fun getItemType(): Int {
        return when (messageType) {
            MessageType.SENDTEXT -> {
                1
            }
            MessageType.RECEVIERTEXT -> {
                2
            }
            MessageType.SENDIMAGE -> {
                3
            }
            MessageType.RECEVIERIMAGE -> {
                4
            }
            MessageType.SENDPRODUCT -> {
                5
            }
            MessageType.RECEVIERPRODUCT -> {
                6
            }
            else -> 0
        }
    }
}