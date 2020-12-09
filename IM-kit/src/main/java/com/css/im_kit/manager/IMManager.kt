package com.css.im_kit.manager

import android.content.Context
import android.util.Log
import com.css.im_kit.callback.SGConversationCallback
import com.css.im_kit.db.bean.Conversation
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.ConversationRepository
import com.css.im_kit.db.repository.MessageRepository
import com.css.im_kit.db.repository.UserInfoRepository
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.model.message.MessageType
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.model.message.TextMessageBody
import com.css.im_kit.model.userinfo.SGUserInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class IMManager(private var context: Context) {

    //回调列表
    private var sgConversationCallbacks = arrayListOf<SGConversationCallback>()

    //会话列表暂存数据
    private var sgConversations = arrayListOf<SGConversation>()

    companion object {
        @Volatile
        private var INSTANCE: IMManager? = null
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
            val sgConversations = arrayListOf<SGConversation>()
            conversations.forEach {
                try {
                    val data = SGConversation()
                    data.conversationId = it.id.toString()
                    val userInfoTask = async { UserInfoRepository.getInstance(context).loadAllById(it.sendUserId) }
                    val messageTask = async { MessageRepository.getInstance(context).getLast(it.sendUserId) }
                    val userInfoResult = userInfoTask.await()
                    val messageResult = messageTask.await()
                    userInfoResult.catch {
                        Log.e("111", "11111111")
                    }.collect {
                        val sgUserinfo = SGUserInfo()
                        sgUserinfo.avatar = it.avatar
                        sgUserinfo.userId = it.userId
                        sgUserinfo.userName = it.nickName
                        data.userInfo = sgUserinfo
                    }
                    messageResult.catch {
                        Log.e("111", "11111111")
                    }.collect {
                        val sgMessage = SGMessage()
                        sgMessage.userInfo = data.userInfo
                        sgMessage.messageId = it.messageId
                        //TODO 需根据后台返回字段处理
                        sgMessage.type = MessageType.TEXT
                        sgMessage.messageBody = TextMessageBody(it.content)
                        data.newMessage = sgMessage
                    }
                    sgConversations.add(data)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            sgConversations.clear()
            sgConversations.addAll(sgConversations)
            sgConversationCallbacks.forEach {
                it.onConversationList(sgConversations)
            }
        }

    }
}