package com.css.im_kit.model.message

import com.css.im_kit.db.bean.CommodityMessage
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.bean.RichBean
import com.css.im_kit.db.bean.SendType
import com.css.im_kit.db.gson
import com.css.im_kit.imservice.bean.DBMessageType
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
                DBMessageType.WELCOME.value -> {
                    TextMessageBody(message.message)
                }
                DBMessageType.NONBUSINESSHOURS.value -> {
                    TextMessageBody(message.message)
                }
                DBMessageType.TEXT.value -> {
                    TextMessageBody(message.message)
                }
                DBMessageType.IMAGE.value -> {
                    ImageMessageBody(message.message)
                }
                DBMessageType.RICH.value -> {
                    try {
                        val commodityMessage = gson.fromJson(message.message, RichBean::class.java)
                        when (commodityMessage.type) {
                            "commodity",
                            "showCommodity" -> {
                                val commodity = gson.fromJson(gson.toJson(commodityMessage.body), CommodityMessage::class.java)
                                CommodityMessageBody.toCommodityMessageBody(commodity)
                            }
                            else -> {
                                TextMessageBody("其他消息类型")
                            }
                        }
                    } catch (e: Exception) {
                        TextMessageBody("其他消息类型")
                    }

                }
                else -> {
                    TextMessageBody("其他消息类型")
                }
            }
            baseMessageBody.isRead = message.read_status == 1
            baseMessageBody.sendTime = message.send_time.toString()
            baseMessageBody.receivedTime = message.receive_time.toString()
            baseMessageBody.sendType = when (message.send_status) {
                1 -> {
                    SendType.SUCCESS
                }
                2 -> {
                    SendType.FAIL
                }
                else -> {
                    SendType.SENDING
                }
            }
            return baseMessageBody
        }
    }

}