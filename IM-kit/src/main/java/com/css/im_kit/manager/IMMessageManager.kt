package com.css.im_kit.manager

import android.util.Log
import com.css.im_kit.IMManager
import com.css.im_kit.callback.MessageCallback
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.bean.RichBean
import com.css.im_kit.db.bean.SendType
import com.css.im_kit.db.gson
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.MessageRepository
import com.css.im_kit.db.repository.UserInfoRepository
import com.css.im_kit.db.uiScope
import com.css.im_kit.http.Retrofit
import com.css.im_kit.imservice.MessageServiceUtils
import com.css.im_kit.imservice.bean.DBMessageType
import com.css.im_kit.imservice.bean.ReceiveMessageBean
import com.css.im_kit.imservice.interfacelinsterner.onResultMessage
import com.css.im_kit.model.message.BaseMessageBody
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.model.userinfo.SGUserInfo
import com.css.im_kit.utils.getExtendS
import com.css.im_kit.utils.log
import com.css.im_kit.utils.toast
import com.qiniu.android.storage.Configuration
import com.qiniu.android.storage.UploadManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.awaitResponse

object IMMessageManager {

    //回调列表
    private var messageCallback = arrayListOf<MessageCallback>()

    /**
     * 开启socket监听
     */
    @Synchronized
    fun openSocketListener() {
        MessageServiceUtils.setOnResultMessage(object : onResultMessage {
            @Synchronized
            override fun onMessage(context: String) {
                Log.e("收到一条消息", context)
                (gson.fromJson(context, ReceiveMessageBean::class.java) ?: null)
                        ?.let { receiveMessage ->
                            //系统返回消息回执
                            when (receiveMessage.type) {
                                DBMessageType.SERVERRECEIPT.value -> {
                                    changeMessageStatus(receiveMessage)
                                    return@let null
                                }
                                DBMessageType.REASSIGNCCUSTOMERSERVICE.value -> {
                                    messageCallback.forEach {
                                        it.on201Message(receiveMessage.toDBMessage())
                                    }
                                }
                                DBMessageType.WELCOME.value,
                                DBMessageType.NONBUSINESSHOURS.value -> {
                                    val message = receiveMessage.toDBMessage()
                                    message.read_status = 1
                                    newMessageNoSave(message)
                                }
                                //新消息
                                else -> {
                                    val message = receiveMessage.toDBMessage()
                                    message.read_status = 1
                                    ioScope.launch {
                                        async {
                                            saveMessage(message, false)
                                        }
                                    }
                                    if (receiveMessage.type != DBMessageType.SERVERRECEIPT.value) {
                                        receiveMessage.type = DBMessageType.CLIENTRECEIPT.value
                                        receiveMessage.send_account = IMManager.account ?: ""
                                        receiveMessage.receive_account = 0.toString()
                                        receiveMessage.source = 6
                                        "收到消息回执:${receiveMessage.toSendMessageBean().toJsonString()}".log()
                                        MessageServiceUtils.sendNewMsg(receiveMessage.toSendMessageBean().toJsonString())
                                    }
                                    return@let message
                                }
                            }
                        }
            }
        })
    }

    /**
     * 分发不存数据库的消息
     */
    fun newMessageNoSave(message: Message) {
        ioScope.launch {
            if (!IMManager.isBusiness) {
                val sgMessage = SGMessage.format(message)
                sgMessage.messageBody = BaseMessageBody.format(message)
                sgMessage.shopId = message.shop_id
                val userInfo = UserInfoRepository.loadById(message.send_account)
                sgMessage.userInfo = SGUserInfo.format(userInfo)
                messageCallback.forEach {
                    it.onReceiveMessage(arrayListOf(sgMessage))
                }
            }
        }
    }


    /**
     * 未读消失数量增加或减少
     */
    @Synchronized
    fun unreadMessageNumCount(shop_id: String, account: String, chat_account: String, size: Int, isClear: Boolean) {
        messageCallback.forEach {
            it.unreadMessageNumCount(shop_id, account, chat_account, size, isClear)
        }
    }

    /**
     * 修改消息状态 成功/失败
     */
    @Synchronized
    private fun changeMessageStatus(message: ReceiveMessageBean) {
        ioScope.launch {
            let {
                return@let if (message.code == 20000) {
                    SendType.SUCCESS
                } else {
                    SendType.FAIL
                }
            }.let {
                MessageRepository.changeMessageSendType(it, message.m_id)
            }
            messageCallback.forEach {
                it.onSendMessageReturn(message.extend?.get("shop_id").toString().toBigDecimal().stripTrailingZeros().toPlainString(), message.m_id)
            }
        }
    }

    /**
     * 添加消息监听
     */
    @Synchronized
    fun addMessageListener(listener: MessageCallback) {
        messageCallback.add(listener)
    }

    /**
     * 添加消息监听
     */
    @Synchronized
    fun send101And102Message(message: Message) {
        ioScope.launch {
            delay(500)
            message.message_type = DBMessageType.WELCOME.value
            "发送WELCOME消息：${message.toSendMessageBean().toJsonString()}".log()
            MessageServiceUtils.sendNewMsg(message.toSendMessageBean().toJsonString())
            message.message_type = DBMessageType.NONBUSINESSHOURS.value
            "发送NONBUSINESSHOURS消息：${message.toSendMessageBean().toJsonString()}".log()
            MessageServiceUtils.sendNewMsg(message.toSendMessageBean().toJsonString())
        }
    }

    /**
     * 删除消息监听
     */
    @Synchronized
    fun removeMessageListener(listener: MessageCallback) {
        messageCallback.remove(listener)
    }

    /**
     * 保存并获取最后一条
     */
    @Synchronized
    private suspend fun saveMessage2DB(message: Message, isSelf: Boolean): SGMessage? {
        message.apply {
            receive_time = System.currentTimeMillis()
        }.let {
            return@let MessageRepository.insert(message)
        }.let { b ->
            if (b) {
                MessageRepository.getMessage4messageId(messageId = message.m_id)?.let {
                    val sgMessage = SGMessage.format(it)
                    sgMessage.messageBody = BaseMessageBody.format(it)
                    sgMessage.shopId = message.shop_id
                    val userInfo = UserInfoRepository.loadById(message.send_account)
                    sgMessage.userInfo = SGUserInfo.format(userInfo)
                    return sgMessage
                }
            }
        }
        return null
    }

    /**
     * 保存消息到数据库
     */
    @Synchronized
    fun saveMessage(message: Message, isSelf: Boolean) {
        ioScope.launch {
            async {
                return@async saveMessage2DB(message, isSelf)
            }.await()?.let { msg ->
                messageCallback.forEach {
                    it.onReceiveMessage(arrayListOf(msg))
                }
                if (isSelf) {
                    Log.e("发送消息", message.toSendMessageBean().toJsonString())
                    MessageServiceUtils.sendNewMsg(message.toSendMessageBean().toJsonString())
                    async {
                        delay(10000)
                        val dbMessage = MessageRepository.getMessage4messageId(message.m_id)
                        dbMessage?.let {
                            if (dbMessage.send_status == 0) {
                                MessageRepository.changeMessageSendType(SendType.FAIL, message.m_id)
                                val extend = gson.fromJson(message.extend, HashMap::class.java)
                                messageCallback.forEach {
                                    it.onSendMessageReturn(extend?.get("shop_id").toString().toBigDecimal().stripTrailingZeros().toPlainString(), message.m_id)
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    /**
     *  保存多条消息到数据库
     */
    @Synchronized
    fun saveMessages(messages: List<Message>, isSelf: Boolean) {
        saveMessages(messages, isSelf, true)
    }

    @Synchronized
    fun saveMessages(messages: List<Message>, isSelf: Boolean, send: Boolean) {
        ioScope.launch {
            let {
                val sgMessages = arrayListOf<SGMessage>()
                messages.forEach {
                    saveMessage2DB(it, isSelf)?.let { it1 ->
                        sgMessages.add(it1)
                    }
                }
                return@let sgMessages
            }.let { msg ->
                messageCallback.forEach {
                    it.onReceiveMessage(msg)
                }
                if (send) {
                    if (isSelf) {
                        messages.forEach { message ->
                            Log.e("发送消息", message.toSendMessageBean().toJsonString())
                            MessageServiceUtils.sendNewMsg(message.toSendMessageBean().toJsonString())
                        }

                        async {
                            delay(10000)
                            messages.forEach { message ->
                                val dbMessage = MessageRepository.getMessage4messageId(message.m_id)
                                dbMessage?.let {
                                    if (dbMessage.send_status == 0) {
                                        MessageRepository.changeMessageSendType(SendType.FAIL, message.m_id)
                                        val extend = gson.fromJson(message.extend, HashMap::class.java)
                                        messageCallback.forEach {
                                            it.onSendMessageReturn(extend?.get("shop_id").toString().toBigDecimal().stripTrailingZeros().toPlainString(), message.m_id)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 消息重发
     */
    @Synchronized
    fun messageReplay(messageId: String) {
        uiScope.launch {
            withContext(Dispatchers.Default) {
                MessageRepository.getMessage4messageId(messageId)?.let { message ->
                    if (message.message_type == DBMessageType.IMAGE.value) {
                        if (!message.message.contains("sgim")) {
                            sendImages(arrayListOf(message), true)
                            return@let null
                        }
                    }
                    message.send_time = System.currentTimeMillis()
                    message.receive_time = System.currentTimeMillis()
                    message.send_status = SendType.SENDING.text
                    MessageRepository.update(message)
                    Log.e("发送消息", message.toSendMessageBean().toJsonString())
                    MessageServiceUtils.sendNewMsg(message.toSendMessageBean().toJsonString())
                    delay(10000)
                    return@let MessageRepository.getMessage4messageId(message.m_id)
                }?.let { message ->
                    if (message.send_status == SendType.SENDING.text) {
                        MessageRepository.changeMessageSendType(SendType.FAIL, message.m_id)
                        val extend = gson.fromJson(message.extend, HashMap::class.java)
                        messageCallback.forEach {
                            it.onSendMessageReturn(extend?.get("shop_id").toString().toBigDecimal().stripTrailingZeros().toPlainString(), message.m_id)
                        }
                    }
                }
            }
        }
    }

    /**
     * 通过展示消息发送商品消息
     */
    @Synchronized
    fun produceShow2Send(messageId: String) {
        uiScope.launch {
            withContext(Dispatchers.Default) {
                MessageRepository.getMessage4messageId(messageId)?.also { message ->
                    MessageRepository.delete(message)
                }?.apply {
                    send_time = System.currentTimeMillis()
                    receive_time = System.currentTimeMillis()
                    send_status = SendType.SENDING.text
                    val produce = gson.fromJson(message, RichBean::class.java)
                    produce.type = "commodity"
                    message = gson.toJson(produce)
                }?.let { message ->
                    saveMessage(message, true)
                }
            }
        }
    }

    /**
     * 上传图片
     */
    fun sendImages(imgMessages: MutableList<Message>, messageReplay: Boolean) {
        ioScope.launch {
            withContext(Dispatchers.Default) {
                val body = HashMap<String, String>()
                // source = "sgim"
                if (IMManager.isBusiness) {
                    body["type"] = "sgim"
                } else {
                    body["source"] = "sgim"
                }
                Retrofit.api?.getQiuNiuTokenUrl(
                        url = IMManager.qiuNiuTokenUrl,
                        requestBody = gson.toJson(body).toRequestBody("application/json".toMediaType())
                )
            }?.awaitResponse()?.let { response ->
                if (response.isSuccessful) {
                    response.body()?.also {
                        if (it.code == "20000") {
                            return@let it.data
                        } else {
                            it.msg.toast()
                        }
                    }
                } else {
                    "请稍后再试".toast()
                }
                return@let null
            }?.let {
                if (!messageReplay) {
                    saveMessages(imgMessages, isSelf = true, send = false)
                }
                upLoadImages(imgMessages, it.token, it.fileName)
            }
        }

    }

    /**
     * 拉取历史记录
     * sgMessage 最后一条消息
     */

    suspend fun getMessageHistory(time: Long, shopId: String, receive_account: String, page: String): List<Message>? {
        return HttpManager.messageHistory(
                shopId = shopId,
                receive_account = receive_account,
                time = time,
                page = page,
                flag = "1",
                pageSize = 30
        )
    }

    /**
     * 更新消息数据到最新
     */
    suspend fun getMessage2New(time: Long, receive_account: String, shopId: String): List<Message>? {
        return HttpManager.messageHistory(
                shopId = shopId,
                receive_account = receive_account,
                time = time,
                page = "1",
                flag = "2",
                pageSize = 10000
        )
    }


    /**
     * 上传图片
     */
    /**
     * 多张图片上传
     *
     * @param pathData
     * @param token
     * @param fileName
     * order_product_id: String?,
    content: String?,
    score: Float?,
    pathData: List<String>,
    token: String?,
    fileName: String
     */
    private fun upLoadImages(pathData: List<Message>,
                             token: String?,
                             fileName: String?) {
        GlobalScope.launch {
            val configuration = Configuration.Builder()
                    .connectTimeout(20) // 链接超时。默认10秒
                    .responseTimeout(20) // 服务器响应超时。默认60秒
                    .build()
            val uploadManager = UploadManager(configuration)
            pathData.asFlow().collect { message ->
                val endexFix: String = getExtendS(message.message) ?: ""
                val fileUrl = fileName + System.currentTimeMillis() + endexFix
                uploadManager.put(message.message, fileUrl, token, { key, info, response ->
                    //res包含hash、key等信息，具体字段取决于上传策略的设置
                    if (info.isOK) {
                        uiScope.launch {
                            withContext(Dispatchers.Default) {
                                message.message = key
                                MessageServiceUtils.sendNewMsg(message.toSendMessageBean().toJsonString())
                                MessageRepository.changeMessageContent(message.m_id, message.message)
                                Log.e("发送消息", message.toSendMessageBean().toJsonString())

                                delay(10000)
                                val dbMessage = MessageRepository.getMessage4messageId(message.m_id)
                                dbMessage?.let {
                                    if (dbMessage.send_status == 0) {
                                        MessageRepository.changeMessageSendType(SendType.FAIL, message.m_id)
                                        val extend = gson.fromJson(message.extend, HashMap::class.java)
                                        messageCallback.forEach {
                                            it.onSendMessageReturn(extend?.get("shop_id").toString().toBigDecimal().stripTrailingZeros().toPlainString(), message.m_id)
                                        }
                                    }
                                }
                            }
                        }

                    } else {
                        ioScope.launch {
                            MessageRepository.changeMessageSendType(SendType.FAIL, message.m_id)
                        }
                    }
                }, null)

            }
        }
    }
}