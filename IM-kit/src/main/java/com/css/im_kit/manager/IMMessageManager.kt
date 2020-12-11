package com.css.im_kit.manager

import android.util.Log
import com.css.im_kit.callback.MessageCallback
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.MessageRepository
import com.css.im_kit.imservice.MessageServiceUtils
import com.css.im_kit.imservice.`interface`.onResultMessage
import com.css.im_kit.model.message.BaseMessageBody
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.model.userinfo.SGUserInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

object IMMessageManager {

    //回调列表
    private var messageCallback = arrayListOf<MessageCallback>()

    /**
     * 开启socket监听
     */
    fun openSocketListener() {
        MessageServiceUtils.setOnResultMessage(object : onResultMessage {
            override fun onMessage(context: String) {
                Log.e("SGIM", context)
            }
        })
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
     * 保存接收到的新消息
     */
    fun rewNewMessage(message: Message) {
        ioScope.launch {
            val task = async {
                message.receivedTime = Date().time.toString()
                MessageRepository.insert(message)
                MessageRepository.getLast(IMChatRoomManager.conversationId!!)
            }
            val result = task.await()
            result?.let { message ->
                val sgMessage = SGMessage.format(message)
                sgMessage.messageBody = BaseMessageBody.format(message)
                if (message.sendUserId == IMChatRoomManager.conversation!!.sendUserId) {
                    sgMessage.userInfo = SGUserInfo.format(IMChatRoomManager.sendUser)
                    sgMessage.messageBody?.isSelf = false
                    sgMessage.messageBody?.isRead = true
                } else {
                    sgMessage.userInfo = SGUserInfo.format(IMChatRoomManager.receiveUser)
                    sgMessage.messageBody?.isSelf = true
                    sgMessage.messageBody?.isRead = true
                }
                messageCallback.forEach {
                    it.onReceiveMessage(sgMessage)
                }
            }

        }
    }

    /**
     * 保存发送的新消息
     */
    fun sendNewMessage(message: Message) {
        ioScope.launch {
            val task = async {
                message.receivedTime = Date().time.toString()
                MessageRepository.insert(message)
                MessageRepository.getLast(IMChatRoomManager.conversationId!!)
            }
            val result = task.await()
            result?.let { message ->
                val sgMessage = SGMessage.format(message)
                sgMessage.messageBody = BaseMessageBody.format(message)
                if (message.sendUserId == IMChatRoomManager.conversation!!.sendUserId) {
                    sgMessage.userInfo = SGUserInfo.format(IMChatRoomManager.sendUser)
                    sgMessage.messageBody?.isSelf = false
                    sgMessage.messageBody?.isRead = true
                } else {
                    sgMessage.userInfo = SGUserInfo.format(IMChatRoomManager.receiveUser)
                    sgMessage.messageBody?.isSelf = true
                    sgMessage.messageBody?.isRead = true
                }
                messageCallback.forEach {
                    it.onReceiveMessage(sgMessage)
                }
            }

        }
    }
}