package com.css.im_kit.manager

import android.content.Context
import android.util.Log
import com.css.im_kit.callback.SGConversationCallback
import com.css.im_kit.db.bean.CommodityMessage
import com.css.im_kit.db.bean.Conversation
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.ConversationRepository
import com.css.im_kit.db.repository.MessageRepository
import com.css.im_kit.db.repository.UserInfoRepository
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.model.message.*
import com.css.im_kit.model.userinfo.SGUserInfo
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class IMManager(private var context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: IMManager? = null

        //回调列表
        private var sgConversationCallbacks = arrayListOf<SGConversationCallback>()

        //会话列表暂存数据
        private var sgConversations = arrayListOf<SGConversation>()

        fun getInstance(context: Context): IMManager {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = IMManager(context)
                }
                return instance
            }
        }
    }

    /**
     * 添加会话列表监听
     */
    fun addSGConversationListListener(listener: SGConversationCallback) {
        sgConversationCallbacks.add(listener)
        //添加监听立即发送暂存数据
        if (!sgConversations.isNullOrEmpty()) {
            listener.onConversationList(sgConversations)
        }
    }

    /**
     * 移除会话列表监听
     */
    fun removeSGConversationListListener(listener: SGConversationCallback) {
        sgConversationCallbacks.remove(listener)
    }

    /**
     * 获取数据库会话列表监听
     */
    fun getConversationList() {
        ioScope.launch {
            ConversationRepository
                    .getInstance(context)
                    .getAll()
                    .catch { cause: Throwable -> Log.e("IMManager", cause.message.toString()) }
                    .collect {
                        integrationConversation(it)
                    }
        }

    }

    private fun integrationConversation(conversations: List<Conversation>) {

        ioScope.launch {
            val task = async {
                val sgConversations = arrayListOf<SGConversation>()
                conversations.forEach {
                    try {
                        val data = SGConversation()
                        data.conversationId = it.id.toString()
                        val userInfoTask = async { UserInfoRepository.getInstance(context).loadAllById(it.sendUserId) }
                        val messageTask = async { MessageRepository.getInstance(context).getLast(it.sendUserId) }
                        val userInfoResult = userInfoTask.await()
                        val messageResult = messageTask.await()
                        userInfoResult?.let {
                            val sgUserinfo = SGUserInfo()
                            sgUserinfo.avatar = userInfoResult.avatar
                            sgUserinfo.userId = userInfoResult.userId
                            sgUserinfo.userName = userInfoResult.nickName
                            data.userInfo = sgUserinfo
                        }
                        messageResult?.let {
                            val sgMessage = SGMessage()
                            sgMessage.userInfo = data.userInfo
                            sgMessage.messageId = messageResult.messageId
                            when (messageResult.type) {
                                MessageType.TEXT.str -> {
                                    sgMessage.type = MessageType.TEXT
                                    sgMessage.messageBody = TextMessageBody(messageResult.content)
                                }
                                MessageType.IMAGE.str -> {
                                    sgMessage.type = MessageType.IMAGE
                                    sgMessage.messageBody = ImageMessageBody(messageResult.content)
                                }
                                MessageType.COMMODITY.str -> {
                                    sgMessage.type = MessageType.TEXT
                                    val message = Gson().fromJson(messageResult.content, CommodityMessage::class.java)
                                    sgMessage.messageBody = CommodityMessageBody.toCommodityMessageBody(message)
                                }
                                else -> {
                                    sgMessage.type = MessageType.TEXT
                                    sgMessage.messageBody = TextMessageBody("其他消息类型")
                                }
                            }
                            data.newMessage = sgMessage
                        }
                        sgConversations.add(data)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                return@async sgConversations
            }
            val result = task.await()
            sgConversations.clear()
            sgConversations.addAll(result)
            sgConversationCallbacks.forEach {
                it.onConversationList(sgConversations)
            }
        }

    }
}