package com.css.im_kit.socket

import android.util.Log
import com.css.im_kit.socket.`interface`.SocketListener
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class JWebSClient(serverUri: URI?,var socketListener: SocketListener?) : WebSocketClient(serverUri, Draft_6455()) {
    /**
     * 连接打开onOpen
     */
    override fun onOpen(handshakedata: ServerHandshake) {

    }
    /**
     * 从远程主机接收的字符串消息的回调
     * @param message
     */
    override fun onMessage(message: String) {

        Log.e("aa", "-------$message")
    }

    /**
     * 在websocket连接关闭后调用
     */
    override fun onClose(code: Int, reason: String, remote: Boolean) {
    }

    /**
     *调用此方法的主要原因是IO或协议错误
     */
    override fun onError(ex: Exception) {

        Log.e("aa", "-------错误 onError")
    }
}