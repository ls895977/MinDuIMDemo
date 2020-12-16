package com.css.im_kit.manager

import com.css.im_kit.callback.MessageCallback
import com.css.im_kit.callback.SGConversationCallback
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.MessageRepository
import com.css.im_kit.db.uiScope
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.model.conversation.Shop
import com.css.im_kit.model.message.MessageType
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.model.message.TextMessageBody
import com.css.im_kit.model.userinfo.SGUserInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object IMConversationManager {

    private var isMyMessageCallbackStart = false

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
        if (!isMyMessageCallbackStart) {
            isMyMessageCallbackStart = true
            IMMessageManager.addMessageListener(myMessageCallback)
        }
    }

    /**
     * 移除会话列表监听
     */
    fun removeSGConversationListListener(listener: SGConversationCallback) {
        sgConversationCallbacks.remove(listener)
        if (sgConversationCallbacks.isNullOrEmpty()) {
            isMyMessageCallbackStart = false
            IMMessageManager.removeMessageListener(myMessageCallback)
        }
    }

    /**
     * 获取数据库会话列表监听
     */
    fun getConversationList() {
        integrationConversation(arrayListOf())
    }

    /**
     * socket新消息监听
     */
    private var myMessageCallback = object : MessageCallback {
        override fun onReceiveMessage(message: SGMessage) {
            uiScope.launch {
                sgConversations.forEach {
                    if (message.shopId == it.shop_id) {
                        val task = async {
                            MessageRepository.getNoReadData(message.shopId)
                        }
                        val result = task.await()
                        it.unread_account = result
                        it.newMessage = message
                        sgConversationCallbacks.forEach { callback ->
                            callback.onConversationList(sgConversations)
                        }
                        return@launch
                    }
                }
            }
        }

        override fun onSendMessageReturn(shop_id:String,messageID: String) {
        }
    }

    private fun integrationConversation(conversations: MutableList<SGConversation>) {

        ioScope.launch {
            val task = async {
                val sGConversation = SGConversation()
                /**
                 *   this.id = id
                this.account = account
                this.chat_account = chat_account
                this.shop_id = shop_id
                this.newMessage = newMessage
                this.unread_account = unread_account
                this.chat_account_info = chat_account_info
                this.shop = shop
                 */
                sGConversation.id = "111"
                sGConversation.account = "8116f90a21"
                sGConversation.chat_account = "183ff3fd37"
                sGConversation.shop_id = "10086"
                sGConversation.newMessage = SGMessage(
                        shopId = "10086",
                        messageId = "22222222",
                        messageBody = TextMessageBody("dgdgdgdf"),
                        type = MessageType.TEXT,
                        userInfo = SGUserInfo(
                                account = "222222",
                                nickname = "ver",
                                user_type = "2412",
                                avatar = "geegege"
                        )
                )
                sGConversation.unread_account = 2
                sGConversation.shop = Shop("111","店铺名","http://testimg.supersg.cn/user/773870855045251072.jpeg")
                conversations.add(sGConversation)
                return@async conversations
            }
            val result = task.await()
            uiScope.launch {
                sgConversations.clear()
                sgConversations.addAll(result)
                sgConversationCallbacks.forEach {
                    it.onConversationList(sgConversations)
                }
            }

        }

    }
}