package com.css.im_kit.model.message

import com.css.im_kit.db.bean.CommodityMessage
import com.css.im_kit.db.bean.Message
import com.google.gson.Gson

open class BaseMessageBody {
    /**
     * 是否新消息
     */
    var isRead: Boolean? = null

    /**
     * 消息接收时间
     */
    var receivedTime: String? = null

    /**
     * 消息发送时间
     */
    var sendTime: String? = null

    /**
     * 是否是自己发送的消息
     */
    var isSelf: Boolean? = null

    constructor()
    constructor(isRead: Boolean?, receivedTime: String?, sendTime: String?, isSelf: Boolean?) {
        this.isRead = isRead
        this.receivedTime = receivedTime
        this.sendTime = sendTime
        this.isSelf = isSelf
    }

    companion object {
        fun format(message: Message): BaseMessageBody {
            val baseMessageBody: BaseMessageBody
            baseMessageBody = when (message.type) {
                MessageType.TEXT.str -> {
                    TextMessageBody(message.content)
                }
                MessageType.IMAGE.str -> {
                    ImageMessageBody(message.content)
                }
                MessageType.COMMODITY.str -> {
                    val commodityMessage = Gson().fromJson(message.content, CommodityMessage::class.java)
                    CommodityMessageBody.toCommodityMessageBody(commodityMessage)
                }
                else -> {
                    TextMessageBody("其他消息类型")
                }
            }
            return baseMessageBody
        }
    }

}