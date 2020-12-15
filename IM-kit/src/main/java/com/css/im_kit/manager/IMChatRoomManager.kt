package com.css.im_kit.manager

import android.util.Log
import com.css.im_kit.IMManager
import com.css.im_kit.callback.ChatRoomCallback
import com.css.im_kit.callback.MessageCallback
import com.css.im_kit.db.bean.CommodityMessage
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.bean.SendType
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.MessageRepository
import com.css.im_kit.db.uiScope
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.model.message.BaseMessageBody
import com.css.im_kit.model.message.MessageType
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.model.userinfo.SGUserInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object IMChatRoomManager {
    var conversation: SGConversation? = null

    //回调列表
    private var chatRoomCallback: ChatRoomCallback? = null

    /**
     * 进入聊天室
     */
    fun initConversation(conversation: SGConversation): IMChatRoomManager {
        this.conversation = conversation
        return this
    }

    /**
     * 添加会话列表监听
     */
    fun addSGConversationListListener(listener: ChatRoomCallback): IMChatRoomManager {
        this.chatRoomCallback = listener
        return this
    }

    /**
     * 移除会话列表监听
     */
    fun dismissSgMessageCallback() {
        this.chatRoomCallback = null
        this.conversation = null
        IMMessageManager.removeMessageListener(myMessageCallback)
    }

    /**
     * socket新消息监听
     */
    private var myMessageCallback = object : MessageCallback {
        override fun onReceiveMessage(message: SGMessage) {
            uiScope.launch {
                MessageRepository.read(arrayListOf(message.messageId))
                message.messageBody?.isRead = true
                chatRoomCallback?.onReceiveMessage(message)
            }
        }
    }

    /**
     * 发送文字消息
     *
    var message_id: String,//消息id
    var send_account: String,//发送账号
    var receive_account: String,//接收账号
    var shop_id: String,//店铺id
    var message_type: String,//消息类型
    var read_status: Boolean,//是否未读
    var send_status: String,//是否发送成功
    var send_time: Long,//发送时间
    var receive_time: Long,//接收时间
    var message: String//消息内容
     */
    fun sendTextMessage(text: String) {
        conversation?.let { conversation ->
            val time = System.currentTimeMillis()
            IMMessageManager.saveMessage(Message(
                    message_id = IMManager.userID + time,
                    send_account = IMManager.userID!!,
                    receive_account = conversation.chat_account ?: "",
                    shop_id = conversation.shop_id ?: "",
                    message_type = MessageType.TEXT.str,
                    read_status = false,
                    send_status = SendType.SENDING.text,
                    send_time = time,
                    receive_time = time,
                    message = text
            ), true)
        }

    }

    /**
     * 发送图片消息
     */
    fun sendImageMessage(img: String) {
        conversation?.let { conversation ->
            val time = System.currentTimeMillis()
            IMMessageManager.saveMessage(Message(
                    message_id = IMManager.userID + time,
                    send_account = IMManager.userID!!,
                    receive_account = conversation.chat_account ?: "",
                    shop_id = conversation.shop_id ?: "",
                    message_type = MessageType.IMAGE.str,
                    read_status = false,
                    send_status = SendType.SENDING.text,
                    send_time = time,
                    receive_time = time,
                    message = img
            ), true)
        }
    }

    /**
     * 发送商品消息
     */
    fun sendCommodityMessage(commodityMessage: CommodityMessage) {
        conversation?.let { conversation ->
            val time = System.currentTimeMillis()
            IMMessageManager.saveMessage(Message(
                    message_id = IMManager.userID + time,
                    send_account = IMManager.userID!!,
                    receive_account = conversation.chat_account ?: "",
                    shop_id = conversation.shop_id ?: "",
                    message_type = MessageType.COMMODITY.str,
                    read_status = false,
                    send_status = SendType.SENDING.text,
                    send_time = time,
                    receive_time = time,
                    message = commodityMessage.toJsonString()
            ), true)
        }

    }


    /**
     * 构建
     */
    fun create() {
        getMessages()
        IMMessageManager.addMessageListener(myMessageCallback)
    }

    /**
     * 获取消息列表
     */
    private fun getMessages() {
        if (conversation == null) {
            Log.e("SGIM", "聊天室为空")
            return
        } else {
            ioScope.launch {
                val task = async {
                    val sgMessages = arrayListOf<SGMessage>()
                    conversation?.let { conversation ->
                        val resultMessage = MessageRepository.getMessage(conversation.shop_id ?: "")
                        resultMessage.forEach { message ->
                            val sgMessage = SGMessage.format(message)
                            sgMessage.messageBody = BaseMessageBody.format(message)
                            if (message.send_account == IMManager.userID) {
                                sgMessage.userInfo = null
                                sgMessage.messageBody?.isSelf = true
                            } else {
                                sgMessage.userInfo = null
                                sgMessage.messageBody?.isSelf = false
                            }
                            sgMessages.add(sgMessage)
                        }
                    }
                    val noReadMessageId = arrayListOf<String>()
                    sgMessages.filter {
                        it.messageBody?.isRead == false
                    }.forEach {
                        it.messageId.let { it1 ->
                            noReadMessageId.add(it1)
                        }
                    }
                    if (!noReadMessageId.isNullOrEmpty()) {
                        MessageRepository.read(noReadMessageId)
                    }
                    sgMessages.forEach {
                        it.messageBody?.isRead = true
                    }
                    return@async sgMessages
                }
                val result = task.await()
                chatRoomCallback?.onMessages(result)
            }
        }
    }
}