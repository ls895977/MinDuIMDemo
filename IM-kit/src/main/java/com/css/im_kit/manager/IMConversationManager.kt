package com.css.im_kit.manager

import com.css.im_kit.IMManager
import com.css.im_kit.callback.MessageCallback
import com.css.im_kit.callback.SGConversationCallback
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.MessageRepository
import com.css.im_kit.db.uiScope
import com.css.im_kit.http.Retrofit
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.utils.generateSignature
import com.css.im_kit.utils.md5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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
     * socket新消息监听
     */
    private var myMessageCallback = object : MessageCallback {
        @Synchronized
        override fun onReceiveMessage(messages: MutableList<SGMessage>) {
            uiScope.launch {
                sgConversations.forEach {
                    messages.forEachIndexed { index, message ->
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
                            return@forEachIndexed
                        }
                    }
                }
            }
        }

        @Synchronized
        override fun onSendMessageReturn(shop_id: String, messageID: String) {
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
                }?.awaitResponse()?.apply {
                    if (isSuccessful) {
                        body()?.let {
                            if (it.code == "20000") {
                                sgConversations.clear()
                                it.data.forEach { item ->
                                    sgConversations.add(item.toSGConversation())
                                }
                                uiScope.launch {
                                    sgConversationCallbacks.forEach { callback ->
                                        callback.onConversationList(sgConversations)
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}