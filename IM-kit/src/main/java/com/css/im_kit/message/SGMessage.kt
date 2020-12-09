package com.css.im_kit.message

import com.css.im_kit.userinfo.SGUserInfo

/**
 * TEXT 文字类型
 * IMAGE 图片类型
 */
enum class MessageType {
    TEXT,
    IMAGE
}

class SGMessage{
    /**
     * 消息id
     */
    var messageId: String? = null

    /**
     * 消息类型
     */
    var type: MessageType? = null

    /**
     * 发送方的个人信息
     */
    var userInfo: SGUserInfo? = null

    /**
     * 消息内容
     */
    var messageBody: BaseMessageBody? = null

    constructor(
            type: MessageType,
            userInfo: SGUserInfo,
            messageBody: BaseMessageBody
    )

    companion object {
        private fun createTextMessageBody(userInfo: SGUserInfo, messageBody: TextMessageBody) = SGMessage(
                MessageType.TEXT,
                userInfo,
                messageBody
        )

        /**
         * 创建图片消息
         */
        private fun createImageMessageBody(userInfo: SGUserInfo, messageBody: ImageMessageBody) = SGMessage(
                MessageType.IMAGE,
                userInfo,
                messageBody
        )

        /**
         * 创建发送图片消息体
         */
        fun createSendImageMessageBody(userInfo: SGUserInfo, imageUrl: String) {
            val body = ImageMessageBody(imageUrl)
            body.isSelf = true
            createImageMessageBody(userInfo, body)
        }

        /**
         * 创建图片消息体
         */
        fun createImageMessageBody(userInfo: SGUserInfo, imageUrl: String) {
            val body = ImageMessageBody(imageUrl)
            createImageMessageBody(userInfo, body)
        }

        /**
         * 创建发送文字消息体
         */
        fun createSendTextMessageBody(userInfo: SGUserInfo, text: String) {
            val body = TextMessageBody(text)
            body.isSelf = true
            createTextMessageBody(userInfo, body)
        }

        /**
         * 创建文字消息体
         */
        fun createTextMessageBody(userInfo: SGUserInfo, text: String) {
            val body = TextMessageBody(text)
            body.isSelf = true
            createTextMessageBody(userInfo, body)
        }
    }
}