package com.css.im_kit.manager

import android.util.Log
import com.css.im_kit.IMManager
import com.css.im_kit.callback.MessageCallback
import com.css.im_kit.db.bean.Message
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
import com.qiniu.android.storage.Configuration
import com.qiniu.android.storage.UploadManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
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
                val receiveMessage = gson.fromJson(context, ReceiveMessageBean::class.java)
                //系统返回消息回执
                when (receiveMessage.type) {
                    DBMessageType.CLIENTRECEIPT.value, DBMessageType.SERVERRECEIPT.value -> {
                        changeMessageStatus(receiveMessage)
                    }
                    //新消息
                    else -> {
                        val message = receiveMessage.toDBMessage()
                        message.read_status = true
                        ioScope.launch {
                            async {
                                saveMessage(message, false)
                            }
                        }

                    }
                }
            }
        })
    }

    /**
     * 修改消息状态 成功/失败
     */
    @Synchronized
    private fun changeMessageStatus(message: ReceiveMessageBean) {
        ioScope.launch {
            if (message.code == 20000) {
                MessageRepository.changeMessageSendType(SendType.SUCCESS, message.m_id)
            } else {
                MessageRepository.changeMessageSendType(SendType.FAIL, message.m_id)
            }
            messageCallback.forEach {
                it.onSendMessageReturn(message.extend?.get("shop_id") ?: "", message.m_id)
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
        message.receive_time = System.currentTimeMillis()
        MessageRepository.insert(message)
        val dbMessage = MessageRepository.getLast()
        dbMessage?.let {
            val sgMessage = SGMessage.format(it)
            sgMessage.messageBody = BaseMessageBody.format(it)
            sgMessage.shopId = message.shop_id
            val userInfo = UserInfoRepository.loadById(message.send_account)
            userInfo?.let { info ->
                sgMessage.userInfo = SGUserInfo.format(info)
                sgMessage.messageBody?.isSelf = isSelf
                return sgMessage
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
            val task = async {
                return@async saveMessage2DB(message, isSelf)
            }
            val result = task.await()
            result?.let { msg ->
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
                                    it.onSendMessageReturn(extend?.get("shop_id").toString(), message.m_id)
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
            val task = async {
                val sgMessages = arrayListOf<SGMessage>()
                messages.forEach {
                    saveMessage2DB(it, isSelf)?.let { it1 ->
                        sgMessages.add(it1)
                    }
                }
                return@async sgMessages
            }
            val result = task.await()
            result.let { msg ->
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
                                            it.onSendMessageReturn(extend?.get("shop_id").toString(), message.m_id)
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
                val dbMessage = MessageRepository.getMessage4messageId(messageId)

                dbMessage?.let { message ->
                    message.send_time = System.currentTimeMillis()
                    message.receive_time = System.currentTimeMillis()
                    message.send_status = SendType.SENDING.text
                    MessageRepository.update(dbMessage)
                    Log.e("发送消息", message.toSendMessageBean().toJsonString())
                    MessageServiceUtils.sendNewMsg(message.toSendMessageBean().toJsonString())

                    delay(10000)
                    val dbMessage1 = MessageRepository.getMessage4messageId(message.m_id)
                    dbMessage1?.let {
                        if (dbMessage1.send_status == SendType.SENDING.text) {
                            MessageRepository.changeMessageSendType(SendType.FAIL, message.m_id)
                            val extend = gson.fromJson(message.extend, HashMap::class.java)
                            messageCallback.forEach {
                                it.onSendMessageReturn(extend?.get("shop_id").toString(), message.m_id)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 上传图片
     */
    fun sendImages(imgMessages: MutableList<Message>) {
        ioScope.launch {
            withContext(Dispatchers.Default) {
                Retrofit.api?.getQiuNiuTokenUrl(
                        url = IMManager.qiuNiuTokenUrl,
                        source = "sgim"
                )
            }?.awaitResponse()?.let { response ->
                if (response.isSuccessful) {
                    response.body()?.also {
                        if (it.code == "20000") {
                            return@let it.data
                        }
                    }
                }
                return@let null
            }?.let {
                saveMessages(imgMessages, isSelf = true, send = false)
                upLoadImages(imgMessages, it.token, it.fileName)
            }
        }

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
        val configuration = Configuration.Builder()
                .connectTimeout(20) // 链接超时。默认10秒
                .responseTimeout(20) // 服务器响应超时。默认60秒
                .build()
        val uploadManager = UploadManager(configuration)
        GlobalScope.launch {
            pathData.asFlow().collect { message ->
                val endexFix: String = getExtendS(message.message) ?: ""
                val fileUrl = fileName + System.currentTimeMillis() + endexFix
                uploadManager.put(message.message, fileUrl, token, { key, info, response ->
                    //res包含hash、key等信息，具体字段取决于上传策略的设置
                    if (info.isOK) {
                        uiScope.launch {
                            withContext(Dispatchers.Default) {
                                message.message = IMManager.getImageBaseUrl() + key
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
                                            it.onSendMessageReturn(extend?.get("shop_id").toString(), message.m_id)
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