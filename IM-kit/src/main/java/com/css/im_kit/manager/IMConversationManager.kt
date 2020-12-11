package com.css.im_kit.manager

import android.util.Log
import com.css.im_kit.callback.SGConversationCallback
import com.css.im_kit.db.bean.Conversation
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.ConversationRepository
import com.css.im_kit.db.repository.MessageRepository
import com.css.im_kit.db.repository.UserInfoRepository
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.model.message.BaseMessageBody
import com.css.im_kit.model.message.MessageType
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.model.userinfo.SGUserInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

object IMConversationManager {

    //回调列表
    private var sgConversationCallbacks = arrayListOf<SGConversationCallback>()

    //会话列表暂存数据
    private var sgConversations = arrayListOf<SGConversation>()

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
                    .getAll()
                    .collect {
                        integrationConversation(it)
                    }
        }

    }

    /**
     * 添加数据
     */
    fun insertOrUpdateConversations(conversations: ArrayList<Conversation>) {
        ioScope.launch {
            val task = async {
                ConversationRepository.insertOrUpdate(conversations)
            }
            val result = task.await()
            if (result) {
                Log.e("1111", "更新成功")
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
                        val userInfoTask = async { UserInfoRepository.loadById(it.sendUserId) }
                        val messageTask = async { MessageRepository.getLast(it.conversationId) }
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
                            sgMessage.messageBody = BaseMessageBody.format(messageResult)
                            sgMessage.type = when (messageResult.type) {
                                MessageType.TEXT.str -> {
                                    MessageType.TEXT
                                }
                                MessageType.IMAGE.str -> {
                                    MessageType.IMAGE
                                }
                                MessageType.COMMODITY.str -> {
                                    MessageType.COMMODITY
                                }
                                else -> {
                                    MessageType.TEXT
                                }
                            }
                            data.newMessage = sgMessage
                        }
                        data.newsNum =  MessageRepository.getNoReadData(it.conversationId)
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