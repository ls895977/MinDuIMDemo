package com.css.im_kit.manager

import com.css.im_kit.IMManager
import com.css.im_kit.callback.MessageCallback
import com.css.im_kit.callback.SGConversationCallback
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.gson
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.UserInfoRepository
import com.css.im_kit.db.uiScope
import com.css.im_kit.http.Retrofit
import com.css.im_kit.http.bean.ChangeServiceAccountBean
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.model.message.MessageType
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.utils.generateSignature
import com.css.im_kit.utils.md5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

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
        integrationConversation()
    }

    /**
     * 判断新信息是否有会话
     */
    @Synchronized
    private fun messageHasConversation(conversation: SGConversation, message: SGMessage): Boolean {
        if (IMManager.isBusiness) {
            if ((conversation.account == message.messageBody?.sendAccount && conversation.chat_account == message.messageBody?.receiveAccount) ||
                    (conversation.account == message.messageBody?.receiveAccount && conversation.chat_account == message.messageBody?.sendAccount)) {
                return true
            }
        } else {
            if (conversation.shop_id == message.shopId) {
                return true
            }
        }
        return false
    }

    /**
     * socket新消息监听
     */
    private var myMessageCallback = object : MessageCallback {
        @Synchronized
        override fun onReceiveMessage(messages: MutableList<SGMessage>) {
            uiScope.launch {
                var hasNewConversationCount = 0
                messages.forEachIndexed { index, message ->
                    if (message.type != MessageType.WELCOME) {
                        sgConversations.map { sgConversation ->
                            if (messageHasConversation(sgConversation, message)) {
                                hasNewConversationCount = hasNewConversationCount.plus(1)
                                if (message.messageBody?.isSelf == false) {
                                    sgConversation.unread_account = sgConversation.unread_account.plus(1)
                                }
                                sgConversation.newMessage = message
                            }
                        }
                    } else {
                        hasNewConversationCount = hasNewConversationCount.plus(1)
                    }
                }
                sgConversationCallbacks.map { callback ->
                    callback.onConversationList(sgConversations)
                }
                if (hasNewConversationCount != messages.size) {
                    integrationConversation()
                }
            }
        }

        @Synchronized
        override fun onSendMessageReturn(shop_id: String, messageID: String) {
        }

        @Synchronized
        override fun unreadMessageNumCount(shop_id: String, account: String, chat_account: String, size: Int, isClear: Boolean) {
            uiScope.launch {
                sgConversations.map {
                    if (IMManager.isBusiness) {
                        if (it.account == account && chat_account == chat_account) {
                            it.unread_account = if (isClear) {
                                0
                            } else {
                                it.unread_account - size
                            }
                        }

                    } else {
                        if (it.shop_id == shop_id) {
                            it.unread_account = if (isClear) {
                                0
                            } else {
                                it.unread_account - size
                            }
                        }
                    }
                    return@map it
                }.let {
                    sgConversationCallbacks.forEach { callback ->
                        callback.onConversationList(it)
                    }
                }
            }
        }

        override fun on201Message(message: Message) {
            val bean = gson.fromJson(message.extend, ChangeServiceAccountBean::class.java)
            sgConversations.map {
                if (it.shop_id == bean.shop_id.toString()) {
                    it.chat_account = bean.account
                    it.chat_account_info?.user_type = bean.user_type.toString()
                    it.chat_account_info?.avatar = bean.avatar
                    it.chat_account_info?.nickname = bean.nickname
                    it.chat_account_info?.account = bean.account
                }
                return@map it
            }.let {
                sgConversationCallbacks.forEach { callback ->
                    callback.onConversationList(it)
                }
            }
        }
    }

    @Synchronized
    private fun integrationConversation() {
        ioScope.launch {
            try {
                withContext(Dispatchers.Default) {
                    val nonceStr = System.currentTimeMillis().toString().md5()
                    val map = HashMap<String, String>()
                    map["account"] = IMManager.account ?: ""
                    map["app_id"] = IMManager.app_id ?: ""
                    map["nonce_str"] = nonceStr
                    Retrofit.api?.chatList(
                            url = IMManager.chatListUrl ?: "",
                            nonce_str = nonceStr,
                            app_id = IMManager.app_id ?: "",
                            account = IMManager.account ?: "",
                            sign = map.generateSignature(IMManager.app_secret ?: "")
                    )
                }?.awaitResponse()?.let {
                    if (it.isSuccessful && it.body()?.code == "20000") {
                        return@let it.body()?.data
                    }
                    return@let null
                }?.let {
                    sgConversations.clear()
                    it.forEach { item ->
                        val sgConversation = item.toSGConversation()
                        sgConversation.chat_account_info?.let { its ->
                            UserInfoRepository.insertOrUpdateUser(its.toDBUserInfo())
                        }
                        sgConversations.add(sgConversation)
                    }
                    sgConversationCallbacks.forEach { callback ->
                        callback.onConversationList(sgConversations)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}