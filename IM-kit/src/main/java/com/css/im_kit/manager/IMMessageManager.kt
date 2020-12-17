package com.css.im_kit.manager

import android.util.Log
import com.css.im_kit.callback.MessageCallback
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.bean.SendType
import com.css.im_kit.db.gson
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.MessageRepository
import com.css.im_kit.db.repository.UserInfoRepository
import com.css.im_kit.db.uiScope
import com.css.im_kit.imservice.MessageServiceUtils
import com.css.im_kit.imservice.bean.DBMessageType
import com.css.im_kit.imservice.bean.ReceiveMessageBean
import com.css.im_kit.imservice.interfacelinsterner.onResultMessage
import com.css.im_kit.model.message.BaseMessageBody
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.model.userinfo.SGUserInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
                        uiScope.launch {
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
            Log.e("修改消息状态", message.m_id)
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