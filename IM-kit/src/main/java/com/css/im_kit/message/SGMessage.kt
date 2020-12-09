package com.css.im_kit.message

import com.css.im_kit.userinfo.SGUserInfo

/**
 * TEXT 文字类型
 * IMAGE 图片类型
 * COMMODITY 商品
 */
enum class MessageType {
    TEXT,
    IMAGE,
    COMMODITY
}

class SGMessage {
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


    constructor(type: MessageType?, userInfo: SGUserInfo?, messageBody: BaseMessageBody?) {
        this.type = type
        this.userInfo = userInfo
        this.messageBody = messageBody
    }

    constructor()

    companion object {
        /**
         * 创建文字消息
         */
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
         * 创建图片消息
         */
        private fun createCommodityMessageBody(userInfo: SGUserInfo, messageBody: CommodityMessageBody) = SGMessage(
                MessageType.COMMODITY,
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
            createTextMessageBody(userInfo, body)
        }


        /**
         * 创建发送商品消息体
         */
        fun createSendCommodityMessageBody(userInfo: SGUserInfo) {
            val body = CommodityMessageBody()
            body.isSelf = true
            createCommodityMessageBody(userInfo, body)
        }

        /**
         * 创建商品消息体
         */
        fun createCommodityMessageBody(userInfo: SGUserInfo) {
            val body = CommodityMessageBody()
            createCommodityMessageBody(userInfo, body)
        }

    }
}