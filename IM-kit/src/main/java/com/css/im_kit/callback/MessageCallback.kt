package com.css.im_kit.callback

import com.css.im_kit.model.message.SGMessage

interface MessageCallback {

    /**
     * 接收消息
     */
    fun onReceiveMessage(message: SGMessage)

    /**
     * 接收到消息发送成功回执
     */
    fun onSendMessageReturn(shop_id: String,messageID: String)
}
