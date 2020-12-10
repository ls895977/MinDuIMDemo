package com.css.im_kit.manager

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.css.im_kit.callback.SGMessageCallback
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.ConversationRepository
import com.css.im_kit.db.repository.MessageRepository
import com.css.im_kit.db.repository.UserInfoRepository
import com.css.im_kit.model.message.BaseMessageBody
import com.css.im_kit.model.message.MessageType
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.model.userinfo.SGUserInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class IMMessageManager {

    companion object {
        private var context: Context? = null
        private var conversationId: String? = null//会话id
        @Volatile
        private var INSTANCE: IMMessageManager? = null

        //回调列表
        private var sgMessageCallback: SGMessageCallback? = null

        fun build(context: Context): IMMessageManager {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    this.context = context
                    instance = IMMessageManager()
                }
                return instance
            }
        }

        /**
         * 进入聊天室
         */
        fun initConversation(conversationId: String): Companion {
            this.conversationId = conversationId
            return this
        }

        /**
         * 添加会话列表监听
         */
        fun addSGConversationListListener(listener: SGMessageCallback): Companion {
            this.sgMessageCallback = listener
            return this
        }

        /**
         * 移除会话列表监听
         */
        fun dismissSgMessageCallback() {
            this.sgMessageCallback = null
            this.conversationId = null
        }

        /**
         * 构建
         */
        fun create() {
            getMessages()
        }

        /**
         * 获取消息列表
         */
        private fun getMessages() {
            if (conversationId.isNullOrEmpty()) {
                Toast.makeText(context, "聊天室为空", Toast.LENGTH_SHORT).show()
                return
            } else {
                ioScope.launch {
                    val task = async {
                        val conversation = ConversationRepository.findConversation(conversationId!!)
                        val sgMessages = arrayListOf<SGMessage>()
                        conversation?.let {
                            val resultMessage = MessageRepository.getMessage(conversationId!!)
                            val sendUser = UserInfoRepository.loadById(conversation.sendUserId)
                            val receiveUser = UserInfoRepository.loadById(conversation.receiveUserId)
                            Log.e("111","111")
                            resultMessage.forEach { message ->
                                val sgMessage = SGMessage()
                                sgMessage.messageId = message.messageId
                                sgMessage.type = when (message.type) {
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
                                if (message.sendUserId == conversation.sendUserId) {
                                    sgMessage.userInfo = SGUserInfo.format(sendUser)
                                    sgMessage.messageBody?.isSelf = false
                                } else {
                                    sgMessage.userInfo = SGUserInfo.format(receiveUser)
                                    sgMessage.messageBody?.isSelf = true
                                }
                                sgMessage.messageBody = BaseMessageBody.format(message)
                                sgMessages.add(sgMessage)
                            }
                        }
                        return@async sgMessages
                    }
                    val result = task.await()
                    sgMessageCallback?.onMessages(result)
                }
            }
        }
    }
}