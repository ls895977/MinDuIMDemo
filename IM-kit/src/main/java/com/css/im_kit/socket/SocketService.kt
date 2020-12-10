package com.css.im_kit.socket

import com.css.im_kit.socket.`interface`.SocketListener
import java.net.URI


object SocketService {
    private var client: JWebSClient? = null
    var socketListener: SocketListener? = null
    /**
     * CHAT_SERVER_URL 聊天服务地址
     */
    fun initSocket(CHAT_SERVER_URL: String?) {
        val uri: URI = URI.create(CHAT_SERVER_URL)
        client = JWebSClient(uri, socketListener)
        client?.connectBlocking()
    }

    /**
     * 发送新消息
     * event 事件名称
     * message 消息内容
     */
    fun sendNewMsg(message: String) {
        if (client != null && client?.isOpen!!) {
            client?.send(message)
        }
    }
    /**
     * 重新链接
     */
    fun connect(){
        try {
            client!!.connectBlocking()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
    /**
     * 断开客户端联系
     */
    fun closeConnect() {
        try {
            if (null != client) {
                client?.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            client = null
        }
    }


}