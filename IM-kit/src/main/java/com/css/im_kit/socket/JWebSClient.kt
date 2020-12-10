package com.css.im_kit.socket
import android.util.Log
import com.css.im_kit.socket.`interface`.SocketListener
import com.css.im_kit.socket.coom.SocketType
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
class JWebSClient(serverUri: URI?) : WebSocketClient(serverUri, Draft_6455()) {
    private var socketListener: SocketListener? = null
    fun setSocketListener(socketListener: SocketListener) {
        this.socketListener = socketListener
    }
    /**
     * 在开始握手并且给定的websocket准备好写入后调用
     */
    override fun onOpen(handshakedata: ServerHandshake) {
        socketListener?.onBackSocketStatus(SocketType.openMessageStats, "已连接")
    }

    /**
     * 从远程主机接收的字符串消息的回调
     * @param message
     */
    override fun onMessage(message: String) {
        socketListener?.onBackSocketStatus(SocketType.collectMessageStats, message)
    }

    /**
     * 在websocket连接关闭后调用
     */
    override fun onClose(code: Int, reason: String, remote: Boolean) {
        socketListener?.onBackSocketStatus(SocketType.closeMessageStats, reason)
    }

    /**
     *调用此方法的主要原因是IO或协议错误
     */
    override fun onError(ex: Exception) {
        socketListener?.onBackSocketStatus(SocketType.errorMessageStats, ex.toString())
    }
}