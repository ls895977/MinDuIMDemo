package com.css.im_kit.model.message

import com.css.im_kit.db.bean.CommodityMessage
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.bean.SendType
import com.css.im_kit.db.gson
import com.google.gson.Gson
import java.io.Serializable

open class BaseMessageBody : Serializable {
    /**
     * 是否新消息
     */
    var isRead: Boolean = false

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
    var isSelf: Boolean = false

    /**
     * 是否是自己发送的消息
     */
    var sendType: SendType = SendType.SENDING

    constructor()
    constructor(isRead: Boolean, receivedTime: String?, sendTime: String?, isSelf: Boolean) {
        this.isRead = isRead
        this.receivedTime = receivedTime
        this.sendTime = sendTime
        this.isSelf = isSelf
    }

    companion object {
        fun format(message: Message): BaseMessageBody {
            val baseMessageBody: BaseMessageBody
            baseMessageBody = when (message.message_type) {
                MessageType.TEXT.str -> {
                    TextMessageBody(message.message)
                }
                MessageType.IMAGE.str -> {
                    ImageMessageBody(message.message)
                }
                MessageType.COMMODITY.str -> {
                    val commodityMessage = gson.fromJson(message.message, CommodityMessage::class.java)
                    CommodityMessageBody.toCommodityMessageBody(commodityMessage)
                }
                else -> {
                    TextMessageBody("其他消息类型")
                }
            }
            baseMessageBody.isRead = message.read_status
            baseMessageBody.sendTime = message.send_time.toString()
            baseMessageBody.receivedTime = message.receive_time.toString()
            return baseMessageBody
        }
    }

}