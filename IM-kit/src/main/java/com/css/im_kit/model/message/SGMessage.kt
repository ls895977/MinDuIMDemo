package com.css.im_kit.model.message

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.bean.RichBean
import com.css.im_kit.db.gson
import com.css.im_kit.imservice.bean.DBMessageType
import com.css.im_kit.model.userinfo.SGUserInfo
import java.io.Serializable
import java.lang.Exception

/**
 * TEXT 文字类型
 * IMAGE 图片类型
 * COMMODITY 商品
 */
enum class MessageType(var str: String) {
    TEXT("TEXT"),
    IMAGE("IMAGE"),
    COMMODITY("COMMODITY"),
    SHOWCOMMODITY("SHOWCOMMODITY");
}

class SGMessage : Serializable, MultiItemEntity {
    /**
     * 会话id
     */
    var shopId: String = ""

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
    constructor(shopId: String, messageId: String, type: MessageType?, userInfo: SGUserInfo?, messageBody: BaseMessageBody?) {
        this.shopId = shopId
        this.messageId = messageId
        this.type = type
        this.userInfo = userInfo
        this.messageBody = messageBody
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    companion object {
        /**
         * 创建文字消息
         */
        private fun createTextMessageBody(conversationId: String, userInfo: SGUserInfo, messageBody: TextMessageBody) = SGMessage(
                conversationId,
                userInfo.account + System.currentTimeMillis(),
                MessageType.TEXT,
                userInfo,
                messageBody
        )

        /**
         * 创建图片消息
         */
        private fun createImageMessageBody(conversationId: String, userInfo: SGUserInfo, messageBody: ImageMessageBody) = SGMessage(
                conversationId,
                userInfo.account + System.currentTimeMillis(),
                MessageType.IMAGE,
                userInfo,
                messageBody
        )

        /**
         * 创建图片消息
         */
        private fun createCommodityMessageBody(conversationId: String, userInfo: SGUserInfo, messageBody: CommodityMessageBody) = SGMessage(
                conversationId,
                userInfo.account + System.currentTimeMillis(),
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
            sgMessage.messageId = message.m_id
            val extend = gson.fromJson(message.extend, HashMap::class.java)
            sgMessage.shopId = extend["shop_id"]?.toString() ?: ""
            sgMessage.type = when (message.message_type) {
                DBMessageType.TEXT.value -> {
                    MessageType.TEXT
                }
                DBMessageType.IMAGE.value -> {
                    MessageType.IMAGE
                }
                DBMessageType.RICH.value -> {
                    try {
                        val commodityMessage = gson.fromJson(message.message, RichBean::class.java)
                        when (commodityMessage.type) {
                            "commodity" -> {
                                MessageType.COMMODITY
                            }
                            "showCommodity" -> {
                                MessageType.SHOWCOMMODITY
                            }
                            else -> {
                                MessageType.TEXT
                            }
                        }
                    }catch (e:Exception){
                        MessageType.TEXT
                    }

                }
                //TODO 下面未实现功能
                DBMessageType.VIDEO.value -> {
                    MessageType.TEXT
                }
                DBMessageType.VOICE.value -> {
                    MessageType.TEXT
                }
                DBMessageType.CLIENTRECEIPT.value -> {
                    MessageType.TEXT
                }
                DBMessageType.SERVERRECEIPT.value -> {
                    MessageType.TEXT
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
                if (messageBody?.isSelf == true) 2 else 5
            }
            MessageType.COMMODITY -> {
                if (messageBody?.isSelf == true) 3 else 6
            }
            MessageType.SHOWCOMMODITY->{
                7
            }
            else -> 0
        }
    }
}