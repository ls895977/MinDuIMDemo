package com.css.im_kit.callback

import com.css.im_kit.model.message.SGMessage

interface ChatRoomCallback {

    /**
     * 接收消息
     */
    fun onReceiveMessage(message: List<SGMessage>)

    /**
     * 全部消息
     */
    fun onMessages(message: List<SGMessage>)

    /**
     * 发送消息进度
     */
    suspend fun onMessageInProgress(message: SGMessage)
}
