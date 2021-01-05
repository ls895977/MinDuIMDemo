package com.css.im_kit.model.message

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.css.im_kit.db.bean.CommodityMessage
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.bean.RichBean
import com.css.im_kit.db.gson
import com.css.im_kit.imservice.bean.DBMessageType
import com.css.im_kit.model.userinfo.SGUserInfo
import java.io.Serializable

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
                                val data = gson.fromJson(gson.toJson(commodityMessage.content), CommodityMessage::class.java)
                                if (data.imgUrl.isNullOrEmpty())
                                    MessageType.TEXT
                                else
                                    MessageType.COMMODITY
                            }
                            else -> {
                                MessageType.TEXT
                            }
                        }
                    } catch (e: Exception) {
                        MessageType.TEXT
                    }
                }
                DBMessageType.WELCOME.value -> {
                    MessageType.TEXT
                }
                DBMessageType.NONBUSINESSHOURS.value -> {
                    MessageType.TEXT
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
            MessageType.SHOWCOMMODITY -> {
                7
            }
            else -> 0
        }
    }
}