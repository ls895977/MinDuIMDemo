package com.css.im_kit.callback

import com.css.im_kit.db.bean.Message
import com.css.im_kit.imservice.bean.ReceiveMessageBean
import com.css.im_kit.model.message.SGMessage

interface MessageCallback {

    /**
     * 接收消息
     */
    fun onReceiveMessage(message: MutableList<SGMessage>)

    fun onSystemMessage(message: MutableList<ReceiveMessageBean>)

    /**
     * 接收到消息发送成功回执
     */
    fun onSendMessageReturn(shop_id: String, messageID: String)

    /**
     * 未读数量计算
     */
    fun unreadMessageNumCount(shop_id: String, account: String, chat_account: String, size: Int, isClear: Boolean)

    /**
     * 重新分配客服
     */
    fun on201Message(message: Message)
}
