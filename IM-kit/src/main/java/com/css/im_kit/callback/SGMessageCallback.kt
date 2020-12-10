package com.css.im_kit.callback

import com.css.im_kit.model.message.SGMessage

interface SGMessageCallback {

    /**
     * 接收消息
     */
    fun onReceiveMessage(message: SGMessage)

    /**
     * 全部消息
     */
    fun onMessages(message: List<SGMessage>)
}
