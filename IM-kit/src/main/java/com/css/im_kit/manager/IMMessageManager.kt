package com.css.im_kit.manager

import android.util.Log
import com.css.im_kit.callback.MessageCallback
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.MessageRepository
import com.css.im_kit.db.repository.UserInfoRepository
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

    private var userId: String? = null

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
     * 保存消息到数据库
     */
    fun saveMessage(message: Message) {
        ioScope.launch {
            val task = async {
                message.receivedTime = System.currentTimeMillis()
                MessageRepository.insert(message)
                val dbMessage = MessageRepository.getLast()
                dbMessage?.let {
                    val sgMessage = SGMessage.format(it)
                    sgMessage.messageBody = BaseMessageBody.format(it)
                    sgMessage.conversationId = message.conversationId
                    val userInfo = UserInfoRepository.loadById(message.sendUserId)
                    userInfo?.let { info ->
                        sgMessage.userInfo = SGUserInfo.format(info)
                        if (info.userId == userId) {
                            sgMessage.messageBody?.isSelf = true
                        }
                        return@async sgMessage
                    }
                }
                return@async null
            }
            val result = task.await()
            result?.let { message ->
                messageCallback.forEach {
                    it.onReceiveMessage(message)
                }
            }
        }
    }
}