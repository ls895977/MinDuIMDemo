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
import com.css.im_kit.imservice.MessageServiceUtils
import com.css.im_kit.imservice.`interface`.onResultMessage
import com.css.im_kit.imservice.bean.ReceiveMessageBean
import com.css.im_kit.model.message.BaseMessageBody
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.model.userinfo.SGUserInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object IMMessageManager {

    //回调列表
    private var messageCallback = arrayListOf<MessageCallback>()

    /**
     * 开启socket监听
     */
    fun openSocketListener() {
        MessageServiceUtils.setOnResultMessage(object : onResultMessage {
            override fun onMessage(context: String) {
                Log.e("收到一条消息", context)
                val receiveMessage = gson.fromJson(context, ReceiveMessageBean::class.java)

                if (receiveMessage.send_account == IMManager.userID) {
                    changeMessageStatus(receiveMessage)
                } else {
                    val message = receiveMessage.toDBMessage()
                    message.read_status = true
                    saveMessage(message, false)
                }
            }
        })
    }

    /**
     * 修改消息状态 成功/失败
     */
    private fun changeMessageStatus(message: ReceiveMessageBean) {
        ioScope.launch {
            if (message.code == 2000) {
                MessageRepository.changeMessageSendType(SendType.SUCCESS, message.m_id)
            }
        }
    }

    /**
     * 添加消息监听
     */
    fun addMessageListener(listener: MessageCallback) {
        messageCallback.add(listener)
    }

    /**
     * 删除消息监听
     */
    fun removeMessageListener(listener: MessageCallback) {
        messageCallback.remove(listener)
    }

    /**
     * 保存消息到数据库
     */
    fun saveMessage(message: Message, isSelf: Boolean) {
        ioScope.launch {
            val task = async {
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
                        return@async sgMessage
                    }
                }
                return@async null
            }
            val result = task.await()
            result?.let { msg ->
                if (isSelf) {
                    MessageServiceUtils.sendNewMsg(message.toSendMessageBean().toJsonString())
                }
                messageCallback.forEach {
                    it.onReceiveMessage(msg)
                }
            }
        }
    }
}