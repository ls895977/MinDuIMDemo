package com.css.im_kit.manager

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.css.im_kit.IMManager
import com.css.im_kit.UnreadCountListener
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await
import java.math.BigDecimal

object IMConversationManager {

    private var isMyMessageCallbackStart = false

    //回调列表
    private var sgConversationCallbacks: SGConversationCallback? = null

    //会话列表暂存数据
    private var sgConversations = ArrayList<MultiItemEntity>()

    private var unreadCountListener: UnreadCountListener? = null

    /**
     * 添加未读数量监听
     */

    fun setUnreadCountListener(listener: UnreadCountListener) {
        unreadCountListener = listener
        unreadCount()
    }

    /**
     * 更新未读数
     */
    private fun unreadCount() {
        unreadCountListener?.apply {
            if (sgConversations.isNullOrEmpty()) {
                unreadCount(0)
                return@apply
            }
            sgConversations.map {
                it as SGConversation
                return@map it.unread_account
            }.reduce { acc, i ->
                acc.plus(i)
            }.let {
                unreadCount(it)
            }
        }
    }

    /**
     * 聊天列表置顶
     */
    fun chatTop(position: Int) {
        uiScope.launch {
            val chatId = (sgConversations[position] as SGConversation).id
            chatId?.let {
                HttpManager.chatTop(it) {
                    synchronized(this) {
                        if (it) {
                            getConversationList()
                        }
                    }
                }
            }
        }
    }

    /**
     * 聊天列表删除
     */
    fun chatDel(position: Int) {
        uiScope.launch {
            val chatId = (sgConversations[position] as SGConversation).id
            chatId?.let {
                HttpManager.chatDel(it) {
                    synchronized(this) {
                        if (it) {
                            sgConversations.filter { conversation ->
                                conversation as SGConversation
                                conversation.id != chatId
                            }.let {
                                sgConversations.clear()
                                sgConversations.addAll(it)
                                sgConversationCallbacks?.onConversationList(sgConversations)
                                unreadCount()
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 添加会话列表监听
     */
    fun addSGConversationListListener(listener: SGConversationCallback) {
        sgConversationCallbacks = listener
        //添加监听立即发送暂存数据
        synchronized(this) {
            if (!sgConversations.isNullOrEmpty()) {
                sgConversations.sortByDescending {
                    if (it.itemType == 1) {
                        it as SGConversation
                        return@sortByDescending it.newMessage?.messageBody?.receivedTime?.toBigDecimal()
                                ?: BigDecimal.ZERO
                    } else {
                        return@sortByDescending Long.MAX_VALUE.toBigDecimal()
                    }
                }
                listener.onConversationList(sgConversations)
            }
        }
        if (!isMyMessageCallbackStart) {
            isMyMessageCallbackStart = true
            IMMessageManager.addMessageListener(myMessageCallback)
        }
    }

    /**
     * 移除会话列表监听
     */
    fun removeSGConversationListListener() {
        sgConversationCallbacks = null
        if (sgConversationCallbacks == null) {
            isMyMessageCallbackStart = false
            IMMessageManager.removeMessageListener(myMessageCallback)
        }
    }

    fun clearSgConversations() {
        synchronized(this) {
            sgConversations.clear()
            sgConversationCallbacks?.onConversationList(sgConversations)
        }
    }

    /**
     * 获取数据库会话列表监听
     */
    fun getConversationList() {
        integrationConversation(false)
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
            synchronized(this) {
                var hasNewConversationCount = 0
                messages.forEachIndexed { index, message ->
                    if (message.type != MessageType.WELCOME) {
                        sgConversations.mapIndexed { index, item ->
                            item as SGConversation
                            if (messageHasConversation(item, message)) {
                                hasNewConversationCount = hasNewConversationCount.plus(1)
                                if (message.messageBody?.isSelf == false) {
                                    val unreadAccount = item.unread_account.plus(1)
                                    (sgConversations[index] as SGConversation).unread_account = unreadAccount
                                }
                                (sgConversations[index] as SGConversation).newMessage = message
                            }
                        }
                    } else {
                        hasNewConversationCount = hasNewConversationCount.plus(1)
                    }
                }
                sgConversations.sortByDescending {
                    if (it.itemType == 1) {
                        it as SGConversation
                        return@sortByDescending it.newMessage?.messageBody?.receivedTime?.toBigDecimal()
                                ?: BigDecimal.ZERO
                    } else {
                        return@sortByDescending Long.MAX_VALUE.toBigDecimal()
                    }
                }

                sgConversationCallbacks?.onConversationList(sgConversations)
                unreadCount()
                if (hasNewConversationCount != messages.size) {
                    integrationConversation(true)
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
                    if (it.itemType == 1) {
                        it as SGConversation
                        if (IMManager.isBusiness) {
                            if (it.account == account && chat_account == chat_account) {
                                it.unread_account = if (isClear) {
                                    0
                                } else {
                                    val unreadSize = it.unread_account - size
                                    if (unreadSize < 0) 0 else unreadSize
                                }
                            }

                        } else {
                            if (it.shop_id == shop_id) {
                                it.unread_account = if (isClear) {
                                    0
                                } else {
                                    val unreadSize = it.unread_account - size
                                    if (unreadSize < 0) 0 else unreadSize
                                }
                            }
                        }
                    }
                    return@map it
                }.let {
                    synchronized(this) {
                        sgConversations.clear()
                        sgConversations.addAll(it)
                        sgConversations.sortByDescending {
                            if (it.itemType == 1) {
                                it as SGConversation
                                return@sortByDescending it.newMessage?.messageBody?.receivedTime?.toBigDecimal()
                            } else {
                                return@sortByDescending Long.MAX_VALUE.toBigDecimal()
                            }
                        }
                        sgConversationCallbacks?.onConversationList(sgConversations)
                        unreadCount()
                    }
                }
            }
        }

        @Synchronized
        override fun on201Message(message: Message) {
            val bean = gson.fromJson(message.extend, ChangeServiceAccountBean::class.java)
            sgConversations.map {
                if (it.itemType == 1) {
                    it as SGConversation
                    if (it.shop_id == bean.shop_id.toString()) {
                        it.chat_account = bean.account
                        it.chat_account_info?.user_type = bean.user_type.toString()
                        it.chat_account_info?.avatar = bean.avatar
                        it.chat_account_info?.nickname = bean.nickname
                        it.chat_account_info?.account = bean.account
                    }
                }
                return@map it
            }.let {
                synchronized(this) {
                    sgConversations.clear()
                    sgConversations.addAll(it)
                    sgConversations.sortByDescending {
                        if (it.itemType == 1) {
                            it as SGConversation
                            return@sortByDescending it.newMessage?.messageBody?.receivedTime?.toBigDecimal()
                        } else {
                            return@sortByDescending Long.MAX_VALUE.toBigDecimal()
                        }
                    }
                    sgConversationCallbacks?.onConversationList(sgConversations)
                    unreadCount()
                }
            }
        }
    }

    @Synchronized
    private fun integrationConversation(isDelay: Boolean) {
        ioScope.launch {
            if (isDelay) delay(1000)
            try {
                withContext(Dispatchers.Default) {
                    val job1 = if (!IMManager.isBusiness) {
                        HttpManager.chatSysMessage()?.await()
                    } else {
                        null
                    }
                    val nonceStr = System.currentTimeMillis().toString().md5()
                    val map = HashMap<String, String>()
                    map["account"] = IMManager.account ?: ""
                    map["app_id"] = IMManager.app_id ?: ""
                    map["nonce_str"] = nonceStr
                    val job2 = Retrofit.api?.chatList(
                            url = IMManager.chatListUrl ?: "",
                            nonce_str = nonceStr,
                            app_id = IMManager.app_id ?: "",
                            account = IMManager.account ?: "",
                            sign = map.generateSignature(IMManager.app_secret ?: "")
                    )?.await()
                    synchronized(this) {
                        sgConversations.clear()
                        job1?.let {
                            if (it.code == "20000") {
                                sgConversations.addAll(it.data)
                            }
                        }
                        job2?.let {
                            if (it.code == "20000") {
                                val datas = arrayListOf<SGConversation>()
                                it.data.forEach { item ->
                                    val sgConversation = item.toSGConversation()
                                    sgConversation.chat_account_info?.let { its ->
                                        ioScope.launch {
                                            UserInfoRepository.insertOrUpdateUser(its.toDBUserInfo())
                                        }
                                    }
                                    datas.add(sgConversation)
                                }
                                datas.sortByDescending {
                                    it.newMessage?.messageBody?.receivedTime?.toBigDecimal()
                                }
                                sgConversations.addAll(datas)
                            }
                        }
                        sgConversationCallbacks?.onConversationList(sgConversations)
                        unreadCount()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}