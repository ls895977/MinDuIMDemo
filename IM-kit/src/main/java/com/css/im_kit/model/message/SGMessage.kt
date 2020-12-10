package com.css.im_kit.model.message

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.css.im_kit.db.bean.Message
import com.css.im_kit.model.userinfo.SGUserInfo

/**
 * TEXT 文字类型
 * IMAGE 图片类型
 * COMMODITY 商品
 */
enum class MessageType(var str: String) {
    TEXT("TEXT"),
    IMAGE("IMAGE"),
    COMMODITY("COMMODITY");
}

class SGMessage : MultiItemEntity {
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

        fun format(message: Message): SGMessage {
            val sgMessage = SGMessage()
            sgMessage.messageId = message.messageId
            sgMessage.type = when (message.type) {
                MessageType.TEXT.str -> {
                    MessageType.TEXT
                }
                MessageType.IMAGE.str -> {
                    MessageType.IMAGE
                }
                MessageType.COMMODITY.str -> {
                    MessageType.COMMODITY
                }
                else -> {
                    MessageType.TEXT
                }
            }
            sgMessage.messageBody = BaseMessageBody.format(message)
            return sgMessage
        }

    }

    /**
     *TEXT
     *IMAGE
     *COMMODITY
     * isSelf是否是自己发送的
     */
    override fun getItemType(): Int {
        return when (type) {
            MessageType.TEXT -> {
                if (messageBody?.isSelf == true) 1 else 4
            }
            MessageType.IMAGE -> {
                if (messageBody!!.isSelf == true) 2 else 5
            }
            MessageType.COMMODITY -> {
                if (messageBody!!.isSelf == true) 3 else 6
            }
            else -> 0
        }
    }
}