package com.css.im_kit.manager

import android.util.Log
import com.css.im_kit.IMManager
import com.css.im_kit.callback.ChatRoomCallback
import com.css.im_kit.callback.MessageCallback
import com.css.im_kit.db.bean.CommodityMessage
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.bean.RichBean
import com.css.im_kit.db.bean.SendType
import com.css.im_kit.db.gson
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.MessageRepository
import com.css.im_kit.db.repository.UserInfoRepository
import com.css.im_kit.db.uiScope
import com.css.im_kit.http.bean.ChangeServiceAccountBean
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
    var httpPage = 1
    var isStart = false
    var time = 0L

    //回调列表
    private var chatRoomCallback: ChatRoomCallback? = null

    /**
     * 进入聊天室
     */
    fun initConversation(conversation: SGConversation): IMChatRoomManager {
        this.conversation = conversation
        httpPage = 1
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
     * 消息重发
     */
    @Synchronized
    fun messageReplay(messageId: String) {
        IMMessageManager.messageReplay(messageId)
    }

    /**
     * 通过展示消息发送商品消息
     */
    @Synchronized
    fun produceShow2Send(messageId: String) {
        IMMessageManager.produceShow2Send(messageId)
    }

    /**
     * 移除会话列表监听
     */
    fun dismissSgMessageCallback() {
        this.chatRoomCallback = null
        this.conversation = null
        httpPage = 1
        time = 0L
        IMMessageManager.removeMessageListener(myMessageCallback)
    }

    /**
     * socket新消息监听
     */
    private var myMessageCallback = object : MessageCallback {
        @Synchronized
        override fun onReceiveMessage(message: MutableList<SGMessage>) {
            uiScope.launch {
                conversation?.let {
                    val ids = arrayListOf<String>()
                    message.filter { item ->
                        if (IMManager.isBusiness) {
                            if (item.messageBody?.sendAccount == it.chat_account || item.messageBody?.receiveAccount == it.chat_account) {
                                return@filter true
                            }
                        } else {
                            if (it.shop_id == item.shopId) {
                                return@filter true
                            }
                        }
                        return@filter false
                    }.map {
                        if (conversation?.shop == null || it.userInfo?.account == IMManager.account) {
                            it.userInfo?.user_type = "1"
                        } else {
                            it.userInfo?.avatar = conversation?.shop?.log
                            it.userInfo?.user_type = "2"
                            if (it.userInfo?.nickname.isNullOrEmpty()) {
                                it.userInfo?.nickname = conversation?.shop?.shop_name
                            }

                        }
                        if (it.messageBody?.isSelf == false) {
                            ids.add(it.messageId)
                        }
                        return@map it
                    }.let {
                        chatRoomCallback?.onReceiveMessage(it)
                    }

                    if (!ids.isNullOrEmpty()) {
                        HttpManager.changRead(conversation!!, ids)
                    }
                }
            }
        }

        @Synchronized
        override fun onSendMessageReturn(shop_id: String, messageID: String) {
            uiScope.launch {
                if (shop_id == conversation?.shop_id) {
                    val task = async {
                        val message = MessageRepository.getMessage4messageId(messageID)
                        message?.let {
                            val sgMessage = SGMessage.format(message)
                            UserInfoRepository.loadById(
                                    if (message.send_account == IMManager.account)
                                        message.send_account
                                    else
                                        message.receive_account
                            )?.let {
                                if (conversation?.shop == null || it.account == IMManager.account) {
                                    sgMessage.userInfo = SGUserInfo(it.account, it.nickname, "1", it.avatar)
                                } else {
                                    sgMessage.userInfo = SGUserInfo(it.account, it.nickname, "2", conversation?.shop?.log)
                                }
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

        override fun unreadMessageNumCount(shop_id: String, account: String, chat_account: String, size: Int, isClear: Boolean) {

        }

        override fun on201Message(message: Message) {
            try {
                val bean = gson.fromJson(message.extend, ChangeServiceAccountBean::class.java)
                conversation?.let {
                    if (it.shop_id == bean.shop_id.toString()) {
                        it.chat_account = bean.account
                        it.chat_account_info?.user_type = bean.user_type.toString()
                        it.chat_account_info?.avatar = bean.avatar
                        it.chat_account_info?.nickname = bean.nickname
                        it.chat_account_info?.account = bean.account
                        it.chat_account_info?.toDBUserInfo()?.let { it1 -> IMUserInfoManager.insertOrUpdateUser(it1) }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }


    @Synchronized
    private fun getMessageSource() {
        ioScope.launch {
            source = if (IMManager.isBusiness) {
                DBMessageSource.SERVICE2USER
            } else {
                DBMessageSource.USER2SERVICE
            }
//            val userInfo = UserInfoRepository.loadById(IMManager.account!!)
//            val chatUserInfo = UserInfoRepository.loadById(conversation?.chat_account!!)
//            if (userInfo != null && chatUserInfo != null) {
//                source = when (userInfo.user_type) {
//                    "1" -> {
//                        when (chatUserInfo.user_type) {
//                            "1" -> {
//                                DBMessageSource.USER2USER
//                            }
//                            else -> DBMessageSource.USER2SERVICE
//                        }
//                    }
//                    "2" -> {
//                        when (chatUserInfo.user_type) {
//                            else -> {
//                                DBMessageSource.SERVICE2USER
//                            }
//                        }
//                    }
//                    else -> {
//                        when (chatUserInfo.user_type) {
//                            "1" -> {
//                                DBMessageSource.SYSTEM2USER
//                            }
//                            else -> DBMessageSource.SYSTEM2SERVICE
//                        }
//                    }
//                }
//            }
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
    @Synchronized
    fun sendTextMessage(text: String) {
        conversation?.let { conversation ->
            val time = System.currentTimeMillis()
            val extend = hashMapOf<Any, Any>()
            extend["shop_id"] = conversation.shop_id.toString()
            IMMessageManager.saveMessage(Message(
                    m_id = (IMManager.account + time).md5().substring(16, 32),
                    send_account = IMManager.account!!,
                    receive_account = conversation.chat_account ?: "",
                    shop_id = conversation.shop_id ?: "",
                    message_type = DBMessageType.TEXT.value,
                    read_status = 0,
                    send_status = SendType.SENDING.text,
                    send_time = time,
                    receive_time = time,
                    message = text,
                    source = source!!.value,
                    extend = gson.toJson(extend)
            ), true)

        }

    }

    /**
     * 发送商品消息
     */
    @Synchronized
    fun sendCommodityMessage(commodityMessages: List<CommodityMessage>) {
        conversation?.let { conversation ->
            var time = System.currentTimeMillis()
            val extend = hashMapOf<Any, Any>()
            extend["shop_id"] = conversation.shop_id.toString()
            commodityMessages.map {
                return@map RichBean("commodity", it)
            }.mapIndexed { index, commodityMessageBean ->
                time += index
                Message(
                        m_id = (IMManager.account + time).md5().substring(16, 32),
                        send_account = IMManager.account!!,
                        receive_account = conversation.chat_account ?: "",
                        shop_id = conversation.shop_id ?: "",
                        message_type = DBMessageType.RICH.value,
                        read_status = 0,
                        send_status = SendType.SENDING.text,
                        send_time = time,
                        receive_time = time,
                        message = commodityMessageBean.toJsonString(),
                        source = source!!.value,
                        extend = gson.toJson(extend)
                )
            }.let {
                IMMessageManager.saveMessages(it, true)
            }

        }
    }

    /**
     * 发送图片消息
     */
    @Synchronized
    private fun sendImageMessage(imgs: List<String>) = ioScope.launch {
        conversation?.let { conversation ->
            val extend = hashMapOf<Any, Any>()
            extend["shop_id"] = conversation.shop_id.toString()
            var time = System.currentTimeMillis()
            imgs.mapIndexed { index, s ->
                time += index
                return@mapIndexed Message(
                        m_id = (IMManager.account + time).md5().substring(16, 32),
                        send_account = IMManager.account!!,
                        receive_account = conversation.chat_account ?: "",
                        shop_id = conversation.shop_id ?: "",
                        message_type = DBMessageType.IMAGE.value,
                        read_status = 0,
                        send_status = SendType.SENDING.text,
                        send_time = time,
                        receive_time = time,
                        message = s,
                        source = source!!.value,
                        extend = gson.toJson(extend)
                )
            }.also {
                IMMessageManager.sendImages(it as MutableList<Message>)
            }
        }
    }


    /**
     * 发送多条图片消息
     */
    @Synchronized
    fun sendImageMessages(imgs: List<String>) {
        sendImageMessage(imgs)
    }

    /**
     * 删除消息
     */
    @Synchronized
    fun removeMessage(messageId: String) {
        ioScope.launch {
            MessageRepository.delete(messageId)
        }
    }


    /**
     * 构建
     */
    fun create() {
        getMessages(System.currentTimeMillis(), 30)
        getMessageSource()
        IMMessageManager.removeMessageListener(myMessageCallback)
        IMMessageManager.addMessageListener(myMessageCallback)
        isStart = true
        if (!IMManager.isBusiness) {
            conversation?.apply {
                val extend = hashMapOf<Any, Any>()
                extend["shop_id"] = shop_id.toString()
                IMMessageManager.send101And102Message(Message(
                        m_id = (IMManager.account + time).md5().substring(16, 32),
                        send_account = IMManager.account!!,
                        receive_account = chat_account ?: "",
                        shop_id = shop_id ?: "",
                        message_type = DBMessageType.TEXT.value,
                        read_status = 0,
                        send_status = SendType.SENDING.text,
                        send_time = time,
                        receive_time = time,
                        message = "1111",
                        source = 1,
                        extend = gson.toJson(extend)
                ))
            }
        }
    }

    /**
     * 消息记录
     */
    fun getMessages(sgMessage: SGMessage?) {
        if (sgMessage == null) return
        if (conversation == null) {
            Log.e("SGIM", "聊天室为空")
            return
        } else {
            sgMessage.messageBody?.receivedTime?.toLong()?.let {
                getMessages(it, 30)
            }
        }
    }

    /**
     * 获取消息列表
     */
    fun getMessages(lastItemTime: Long, pageSize: Int) {
        if (conversation == null) {
            Log.e("SGIM", "聊天室为空")
            return
        } else {
            ioScope.launch {
                async {
                    conversation?.let { conversation ->
                        //把和某人的消息置为已读
                        return@async also {
                            if (isStart) {
                                isStart = false
                                val b = HttpManager.changReadSomeOne(
                                        conversation.chat_account ?: ""
                                )
                                //通知会话列表清空未读消息数
                                if (b) {
                                    IMMessageManager.unreadMessageNumCount(
                                            conversation.shop_id ?: "",
                                            conversation.account ?: "",
                                            conversation.chat_account ?: "",
                                            0,
                                            true)
                                }
                            }
                        }.let {
                            //获取数据库聊天消息 isBusiness 是否是商戶端
                            if (IMManager.isBusiness) {
                                MessageRepository.getMessage4Account(
                                        chat_account = conversation.chat_account ?: "",
                                        lastItemTime = lastItemTime,
                                        pageSize = pageSize
                                )
                            } else {
                                MessageRepository.getMessage(
                                        conversation.shop_id ?: "",
                                        lastItemTime = lastItemTime,
                                        pageSize = pageSize
                                )
                            }
                        }.let {
                            //获取历史记录
                            if (it.isNullOrEmpty()) {
                                if (httpPage == 1) {
                                    time = lastItemTime
                                }
                                val messages = IMMessageManager.getMessageHistory(
                                        time = time,
                                        shopId = conversation.shop_id ?: "",
                                        page = httpPage.toString(),
                                        receive_account = conversation.chat_account ?: ""
                                )
                                if (!it.isNullOrEmpty()) httpPage++
                                return@let messages
                            } else {
                                return@let it
                            }
                        }?.map {
                            //组装user
                            val sgMessage = SGMessage.format(it)
                            val user = UserInfoRepository.loadById(it.send_account)
                            user?.let {
                                if (IMManager.isBusiness) {
                                    sgMessage.userInfo = SGUserInfo(it.account, it.nickname, it.user_type, it.avatar)
                                } else {
                                    sgMessage.userInfo = SGUserInfo(it.account, it.nickname, it.user_type, if (it.user_type == "2") conversation.shop?.log else it.avatar)
                                }
                            }
                            return@map sgMessage
                        }?.map { sgMessage ->
                            sgMessage.messageBody?.isRead = true
                            return@map sgMessage
                        }
                    }
//                    val noReadMessageId = arrayListOf<String>()
//                    sgMessages.filter {
//                        it.messageBody?.isRead == false && it.messageBody?.isSelf != true
//                    }.forEach {
//                        noReadMessageId.add(it.messageId)
//                    }
//                    if (!noReadMessageId.isNullOrEmpty()) {
//                        HttpManager.changRead(noReadMessageId)
//                        IMMessageManager.unreadMessageNumCount(
//                                conversation?.shop_id ?: "",
//                                conversation?.account ?: "",
//                                conversation?.chat_account ?: "",
//                                false,
//                                noReadMessageId.size)
//                    }
                }.await()?.apply {
                    uiScope.launch {
                        chatRoomCallback?.onMessages(lastItemTime, this@apply)
                    }
                }
            }
        }
    }


}

class QNTokenBean {
    var token: String? = null
    var fileName: String? = null
}