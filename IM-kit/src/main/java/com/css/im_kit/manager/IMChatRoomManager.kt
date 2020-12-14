package com.css.im_kit.manager

import android.util.Log
import com.css.im_kit.callback.ChatRoomCallback
import com.css.im_kit.callback.MessageCallback
import com.css.im_kit.db.bean.Conversation
import com.css.im_kit.db.bean.UserInfo
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.ConversationRepository
import com.css.im_kit.db.repository.MessageRepository
import com.css.im_kit.db.repository.UserInfoRepository
import com.css.im_kit.db.uiScope
import com.css.im_kit.model.message.BaseMessageBody
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.model.userinfo.SGUserInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object IMChatRoomManager {
    var conversationId: String? = null//会话id
    var sendUser: UserInfo? = null
    var receiveUser: UserInfo? = null
    var conversation: Conversation? = null

    //回调列表
    private var chatRoomCallback: ChatRoomCallback? = null

    /**
     * 进入聊天室
     */
    fun initConversation(conversationId: String): IMChatRoomManager {
        this.conversationId = conversationId
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
        this.conversationId = null
        this.sendUser = null
        this.receiveUser = null
        this.conversation = null
        IMMessageManager.removeMessageListener(myMessageCallback)
    }

    /**
     * socket新消息监听
     */
    private var myMessageCallback = object : MessageCallback {
        override fun onReceiveMessage(message: SGMessage) {
            uiScope.launch {
                MessageRepository.read(arrayListOf(message.messageId!!))
                message.messageBody?.isRead = true
                chatRoomCallback?.onReceiveMessage(message)
            }
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
        if (conversationId.isNullOrEmpty()) {
            Log.e("SGIM", "聊天室为空")
            return
        } else {
            ioScope.launch {
                val task = async {
                    conversation = ConversationRepository.findConversation(conversationId!!)
                    val sgMessages = arrayListOf<SGMessage>()
                    conversation?.let {
                        val resultMessage = MessageRepository.getMessage(conversationId!!)
                        sendUser = UserInfoRepository.loadById(conversation!!.sendUserId)
                        receiveUser = UserInfoRepository.loadById(conversation!!.receiveUserId)
                        resultMessage.forEach { message ->
                            val sgMessage = SGMessage.format(message)
                            sgMessage.messageBody = BaseMessageBody.format(message)
                            if (message.sendUserId == conversation!!.sendUserId) {
                                sgMessage.userInfo = SGUserInfo.format(sendUser)
                                sgMessage.messageBody?.isSelf = false
                            } else {
                                sgMessage.userInfo = SGUserInfo.format(receiveUser)
                                sgMessage.messageBody?.isSelf = true
                            }
                            sgMessages.add(sgMessage)
                        }
                    }
                    val noReadMessageId = arrayListOf<String>()
                    sgMessages.filter {
                        it.messageBody?.isRead == false
                    }.forEach {
                        it.messageId?.let { it1 ->
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