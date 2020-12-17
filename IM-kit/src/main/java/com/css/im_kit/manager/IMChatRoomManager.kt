package com.css.im_kit.manager

import android.util.Log
import com.css.im_kit.IMManager
import com.css.im_kit.callback.ChatRoomCallback
import com.css.im_kit.callback.MessageCallback
import com.css.im_kit.db.bean.CommodityMessage
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.bean.SendType
import com.css.im_kit.db.gson
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.MessageRepository
import com.css.im_kit.db.repository.UserInfoRepository
import com.css.im_kit.db.uiScope
import com.css.im_kit.imservice.bean.DBMessageSource
import com.css.im_kit.imservice.bean.DBMessageType
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.model.userinfo.SGUserInfo
import com.css.im_kit.utils.md5
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object IMChatRoomManager {
    var conversation: SGConversation? = null
    var source: DBMessageSource? = null

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

        override fun onSendMessageReturn(shop_id: String, messageID: String) {
            uiScope.launch {
                if (shop_id == conversation?.shop_id) {
                    val task = async {
                        Log.e("发送消息回执1", messageID)
                        val message = MessageRepository.getMessage4messageId(messageID)
                        Log.e("发送消息回执2", message?.m_id ?: "")
                        message?.let {
                            val sgMessage = SGMessage.format(message)
                            if (message.send_account == IMManager.account) {
                                sgMessage.messageBody?.isSelf = true
                            }
                            val user = UserInfoRepository.loadById(
                                    if (message.send_account == IMManager.account)
                                        message.send_account
                                    else
                                        message.receive_account
                            )
                            user?.let {
                                sgMessage.userInfo = SGUserInfo(it.account, it.nickname, it.user_type, it.avatar)
                            }
                            return@async sgMessage
                        }
                    }
                    val result = task.await()
                    result?.let {
                        chatRoomCallback?.onMessageInProgress(it)
                    }
                }
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
            val extend = hashMapOf<Any, Any>()
            extend["shop_id"] = conversation.shop_id.toString()
            ioScope.launch {
                async {
                    IMMessageManager.saveMessage(Message(
                            m_id = (IMManager.account + time).md5().substring(16,32),
                            send_account = IMManager.account!!,
                            receive_account = conversation.chat_account ?: "",
                            shop_id = conversation.shop_id ?: "",
                            message_type = DBMessageType.TEXT.value,
                            read_status = false,
                            send_status = SendType.SENDING.text,
                            send_time = time,
                            receive_time = time,
                            message = text,
                            source = source!!.value,
                            extend = gson.toJson(extend)
                    ), true)
                }
            }

        }

    }

    private fun getMessageSource() {
        ioScope.launch {
            val userInfo = UserInfoRepository.loadById(IMManager.account!!)
            val chatUserInfo = UserInfoRepository.loadById(conversation?.chat_account!!)
            if (userInfo != null && chatUserInfo != null) {
                source = when (userInfo.user_type) {
                    "1" -> {
                        when (chatUserInfo.user_type) {
                            "1" -> {
                                DBMessageSource.USER2USER
                            }
                            else -> DBMessageSource.USER2SERVICE
                        }
                    }
                    "2" -> {
                        when (chatUserInfo.user_type) {
                            else -> {
                                DBMessageSource.SERVICE2USER
                            }
                        }
                    }
                    else -> {
                        when (chatUserInfo.user_type) {
                            "1" -> {
                                DBMessageSource.SYSTEM2USER
                            }
                            else -> DBMessageSource.SYSTEM2SERVICE
                        }
                    }
                }
            }
        }
    }

    /**
     * 发送图片消息
     */
    private fun sendImageMessage(img: String, time: Long) = ioScope.launch {
        async {
            conversation?.let { conversation ->
                val extend = hashMapOf<Any, Any>()
                extend["shop_id"] = conversation.shop_id.toString()

                IMMessageManager.saveMessage(Message(
                        m_id = (IMManager.account + time).md5().substring(16,32),
                        send_account = IMManager.account!!,
                        receive_account = conversation.chat_account ?: "",
                        shop_id = conversation.shop_id ?: "",
                        message_type = DBMessageType.IMAGE.value,
                        read_status = false,
                        send_status = SendType.SENDING.text,
                        send_time = time,
                        receive_time = time,
                        message = img,
                        source = source!!.value,
                        extend = gson.toJson(extend)
                ), true)
            }
        }
    }

    /**
     * 发送图片消息
     */
    fun sendImageMessage(img: String) {
        sendImageMessage(img, System.currentTimeMillis())
    }

    /**
     * 发送多条图片消息
     */
    fun sendImageMessages(imgs: List<String>) {
        val time = System.currentTimeMillis()
        imgs.forEachIndexed { index, s ->
            sendImageMessage(s, System.currentTimeMillis() + index)
        }
    }


    /**
     * 发送商品消息
     */
    fun sendCommodityMessage(commodityMessage: CommodityMessage) {
        conversation?.let { conversation ->
            val time = System.currentTimeMillis()
            val extend = hashMapOf<Any, Any>()
            extend["shop_id"] = conversation.shop_id.toString()
            ioScope.launch {
                async {
                    IMMessageManager.saveMessage(Message(
                            m_id = (IMManager.account + time).md5(),
                            send_account = IMManager.account!!,
                            receive_account = conversation.chat_account ?: "",
                            shop_id = conversation.shop_id ?: "",
                            message_type = DBMessageType.RICH.value,
                            read_status = false,
                            send_status = SendType.SENDING.text,
                            send_time = time,
                            receive_time = time,
                            message = commodityMessage.toJsonString(),
                            source = source!!.value,
                            extend = gson.toJson(extend)
                    ), true)
                }
            }
        }

    }


    /**
     * 构建
     */
    fun create() {
        getMessages()
        getMessageSource()
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
                            sgMessage.messageBody?.isSelf = message.send_account == IMManager.account
                            val user = UserInfoRepository.loadById(message.send_account)
                            user?.let {
                                sgMessage.userInfo = SGUserInfo(it.account, it.nickname, it.user_type, it.avatar)
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
                uiScope.launch {
                    chatRoomCallback?.onMessages(result)
                }
            }
        }
    }
}