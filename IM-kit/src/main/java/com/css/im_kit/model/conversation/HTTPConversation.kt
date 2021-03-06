package com.css.im_kit.model.conversation

import com.css.im_kit.db.bean.CommodityMessage
import com.css.im_kit.db.gson
import com.css.im_kit.imservice.bean.DBMessageType
import com.css.im_kit.model.message.*
import com.css.im_kit.model.userinfo.SGUserInfo
import java.io.Serializable

/**
 * 会话列表
 *
id	number
非必须
account	string
非必须
chat_account	string
非必须
shop_id	number
非必须
message_type	number
非必须
created_time	string
非必须
updated_time	string
非必须
data_state	string
非必须
content	string
非必须
聊天内容
unread_account	number
非必须
chat_account_info	object
非必须
聊天人的信息
 *
 */
class HTTPConversation : Serializable {

    /**
     * 会话列表id
     */
    var id: String? = null

    /**
     * 自己的聊天账号
     */
    var account: String? = null

    /**
     *对方聊天账号
     */
    var chat_account: String? = null

    /**
     * 店铺id
     */
    var shop_id: String? = null


    /**
     * 消息类型
     */
    var message_type: Int = 0

    /**
     * 消息创建时间
     */
    var created_time: String? = null
    var updated_time: String? = null
    var data_state: String? = null

    /**
     * 聊天内容
     */
    var content: String? = null

    /**
     *置顶字段，0 是不置顶
     */
    var sort: Int = 0

    /**
     * 新消息数量
     */
    var unread_account: Int = 0

    /**
     * 用户
     */
    var chat_account_info: SGUserInfo? = null

    /**
     * 店铺信息
     */
    var shop: Shop? = null

    constructor()
    constructor(id: String?, account: String?, chat_account: String?, shop_id: String?, message_type: Int, created_time: String?, updated_time: String?, data_state: String?, content: String?, unread_account: Int, chat_account_info: SGUserInfo?, shop: Shop?) {
        this.id = id
        this.account = account
        this.chat_account = chat_account
        this.shop_id = shop_id
        this.message_type = message_type
        this.created_time = created_time
        this.updated_time = updated_time
        this.data_state = data_state
        this.content = content
        this.unread_account = unread_account
        this.chat_account_info = chat_account_info
        this.shop = shop
    }

    /**
    this.id = id
    this.account = account
    this.chat_account = chat_account
    this.shop_id = shop_id
    this.newMessage = newMessage
    this.unread_account = unread_account
    this.chat_account_info = chat_account_info
    this.shop = shop

    this.shopId = shopId
    this.messageId = messageId
    this.type = type
    this.userInfo = userInfo
    this.messageBody = messageBody

    this.account = account
    this.nickname = nickname
    this.user_type = user_type
    this.avatar = avatar
    isRead: Boolean, receivedTime: String, sendTime: String, isSelf: Boolean, text: String
     */

    fun toSGConversation(): SGConversation {
        var messageType: MessageType
        val baseMessageBody: BaseMessageBody = when (message_type) {
            DBMessageType.TEXT.value -> {
                messageType = MessageType.TEXT
                val item = gson.fromJson(content, ConversationMessage::class.java)
                TextMessageBody(
                        sendAccount = item.send_account,
                        receiveAccount = item.receive_account,
                        isRead = true,
                        receivedTime = item.time,
                        sendTime = item.time,
                        text = item.content
                )
            }
            DBMessageType.IMAGE.value -> {
                messageType = MessageType.IMAGE
                val item = gson.fromJson(content, ConversationMessage::class.java)
                ImageMessageBody(
                        sendAccount = item.send_account,
                        receiveAccount = item.receive_account,
                        isRead = true,
                        receivedTime = item.time,
                        sendTime = item.time,
                        imageUrl = item.content
                )
            }
            DBMessageType.RICH.value -> {
                try {
                    val item = gson.fromJson(content, ConversationMessage::class.java)
                    val messageBean = gson.fromJson(item.content, HashMap::class.java)
                    if (messageBean["type"] == "commodity") {

                        val commodity = gson.fromJson(gson.toJson(messageBean["content"]), CommodityMessage::class.java)
                        if (commodity.imgUrl.isNullOrEmpty()) {
                            messageType = MessageType.TEXT
                            TextMessageBody(
                                    sendAccount = item.send_account,
                                    receiveAccount = item.receive_account,
                                    isRead = true,
                                    receivedTime = item.time,
                                    sendTime = item.time,
                                    text = "未知消息类型"
                            )
                        } else {
                            messageType = MessageType.COMMODITY
                            CommodityMessageBody(
                                    sendAccount = item.send_account,
                                    receiveAccount = item.receive_account,
                                    isRead = true,
                                    receivedTime = item.time,
                                    sendTime = item.time,
                                    commodityId = commodity.productId,
                                    commodityImage = commodity.imgUrl,
                                    commodityName = commodity.productName,
                                    commodityPrice = commodity.salePrice
                            )
                        }
                    } else {
                        messageType = MessageType.TEXT
                        TextMessageBody(
                                sendAccount = item.send_account,
                                receiveAccount = item.receive_account,
                                isRead = true,
                                receivedTime = item.time,
                                sendTime = item.time,
                                text = "未知类型"
                        )
                    }
                } catch (e: Exception) {
                    val item = gson.fromJson(content, ConversationMessage::class.java)
                    messageType = MessageType.TEXT
                    TextMessageBody(
                            sendAccount = item.send_account,
                            receiveAccount = item.receive_account,
                            isRead = true,
                            receivedTime = item.time,
                            sendTime = item.time,
                            text = "未知类型")
                }

            }
            DBMessageType.WELCOME.value -> {
                messageType = MessageType.TEXT
                val item = gson.fromJson(content, ConversationMessage::class.java)
                TextMessageBody(
                        sendAccount = item.send_account,
                        receiveAccount = item.receive_account,
                        isRead = true,
                        receivedTime = item.time,
                        sendTime = item.time,
                        text = item.content
                )
            }
            DBMessageType.NONBUSINESSHOURS.value -> {
                messageType = MessageType.TEXT
                val item = gson.fromJson(content, ConversationMessage::class.java)
                TextMessageBody(
                        sendAccount = item.send_account,
                        receiveAccount = item.receive_account,
                        isRead = true,
                        receivedTime = item.time,
                        sendTime = item.time,
                        text = item.content
                )
            }
            else -> {
                messageType = MessageType.TEXT
                val item = gson.fromJson(content, ConversationMessage::class.java)
                TextMessageBody(
                        sendAccount = item.send_account,
                        receiveAccount = item.receive_account,
                        isRead = true,
                        receivedTime = item.time,
                        sendTime =item.time,
                        text = "未知类型"
                )
            }
        }
        val message = SGMessage(
                shopId = shop_id ?: "",
                messageId = "",
                type = messageType,
                userInfo = null,
                messageBody = baseMessageBody
        )

        return SGConversation(
                id = id,
                account = account,
                chat_account = chat_account,
                shop_id = shop_id,
                newMessage = message,
                sort = sort,
                unread_account = unread_account,
                chat_account_info = chat_account_info,
                shop = shop
        )
    }
}

class ConversationMessage(
        val content: String,
        val m_id: String,
        val receive_account: String,
        val send_account: String,
        val source: String,
        val time: String,
        val type: String
)

