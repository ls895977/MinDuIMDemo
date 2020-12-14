package com.css.im_kit.model.message

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.css.im_kit.db.bean.Message
import com.css.im_kit.model.userinfo.SGUserInfo
import java.util.*

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
     * 会话id
     */
    var conversationId: String = ""

    /**
     * 消息id
     */
    var messageId: String = ""

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

    constructor()
    constructor(conversationId: String, messageId: String, type: MessageType?, userInfo: SGUserInfo?, messageBody: BaseMessageBody?) {
        this.conversationId = conversationId
        this.messageId = messageId
        this.type = type
        this.userInfo = userInfo
        this.messageBody = messageBody
    }

    companion object {
        /**
         * 创建文字消息
         */
        private fun createTextMessageBody(conversationId: String, userInfo: SGUserInfo, messageBody: TextMessageBody) = SGMessage(
                conversationId,
                userInfo.userId + System.currentTimeMillis(),
                MessageType.TEXT,
                userInfo,
                messageBody
        )

        /**
         * 创建图片消息
         */
        private fun createImageMessageBody(conversationId: String, userInfo: SGUserInfo, messageBody: ImageMessageBody) = SGMessage(
                conversationId,
                userInfo.userId + System.currentTimeMillis(),
                MessageType.IMAGE,
                userInfo,
                messageBody
        )

        /**
         * 创建图片消息
         */
        private fun createCommodityMessageBody(conversationId: String, userInfo: SGUserInfo, messageBody: CommodityMessageBody) = SGMessage(
                conversationId,
                userInfo.userId + System.currentTimeMillis(),
                MessageType.COMMODITY,
                userInfo,
                messageBody
        )

        /**
         * 创建发送图片消息体
         */
        fun createSendImageMessageBody(conversationId: String, userInfo: SGUserInfo, imageUrl: String) {
            val body = ImageMessageBody(imageUrl)
            body.isSelf = true
            createImageMessageBody(conversationId, userInfo, body)
        }

        /**
         * 创建图片消息体
         */
        fun createImageMessageBody(conversationId: String, userInfo: SGUserInfo, imageUrl: String): SGMessage {
            val body = ImageMessageBody(imageUrl)
            return createImageMessageBody(conversationId, userInfo, body)
        }

        /**
         * 创建发送文字消息体
         */
        fun createSendTextMessageBody(conversationId: String, userInfo: SGUserInfo, text: String): SGMessage {
            val body = TextMessageBody(text)
            body.isSelf = true
            return createTextMessageBody(conversationId, userInfo, body)
        }

        /**
         * 创建文字消息体
         */
        fun createTextMessageBody(conversationId: String, userInfo: SGUserInfo, text: String) {
            val body = TextMessageBody(text)
            createTextMessageBody(conversationId, userInfo, body)
        }


        /**
         * 创建发送商品消息体
         */
        fun createSendCommodityMessageBody(conversationId: String, userInfo: SGUserInfo) {
            val body = CommodityMessageBody()
            body.isSelf = true
            createCommodityMessageBody(conversationId, userInfo, body)
        }

        /**
         * 创建商品消息体
         */
        fun createCommodityMessageBody(conversationId: String, userInfo: SGUserInfo) {
            val body = CommodityMessageBody()
            createCommodityMessageBody(conversationId, userInfo, body)
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