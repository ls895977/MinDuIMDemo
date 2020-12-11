package com.css.im_kit.callback

import com.css.im_kit.model.message.SGMessage

interface MessageCallback {

    /**
     * 接收消息
     */
    fun onReceiveMessage(message: SGMessage)
}
