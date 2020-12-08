package com.css.im_kit.socket
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException
object SocketService {
    private var mSocket: Socket? = null

    /**
     * 初始化Socket尽量在Application做处理
     * CHAT_SERVER_URL 聊天服务地址
     */
    fun initSocket(CHAT_SERVER_URL: String?) {
        try {
            mSocket = IO.socket(CHAT_SERVER_URL)
            socketMonitoring()
            mSocket?.connect()//实现连接
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
    }

    /**
     * 监听连接状态和消息接收
     */
    private fun socketMonitoring() {
        mSocket?.on(Socket.EVENT_CONNECT) {//监听连接
            Log.e("aa", "--------------监听连接")
        }?.on(Socket.EVENT_CONNECTING) {//连接中
            Log.e("aa", "--------------连接中")
        }?.on(Socket.EVENT_DISCONNECT) {//监听断开连接
            Log.e("aa", "--------------监听断开连接")
        }?.on(Socket.EVENT_ERROR) {//事件错误
            Log.e("aa", "--------------事件错误")
        }?.on(Socket.EVENT_CONNECT_TIMEOUT) {//在连接超时时调用
            Log.e("aa", "--------------在连接超时时调用===")
        }?.on(Socket.EVENT_CONNECT_ERROR) {//连接错误
            Log.e("aa", "--------------连接错误")
        }?.on(Socket.EVENT_RECONNECT) {//在重新连接成功时调用
            Log.e("aa", "--------------在重新连接成功时调用")
        }?.on(Socket.EVENT_RECONNECT) {//在重新连接成功时调用
            Log.e("aa", "--------------在重新连接成功时调用")
        }
//        mSocket?.on(Manager.EVENT_TRANSPORT) {
//            val transport = it[0] as Transport
//            transport.on(Transport.EVENT_REQUEST_HEADERS) {
//            }
//        }
//        mSocket?.on(Transport.EVENT_RESPONSE_HEADERS) { args ->
//            val headers = args[0] as Map<String, List<String>>
//            val cookie = (headers["Set-Cookie"] ?: error(""))[0]//获取cookie
//        }
    }

    /**
     * 发送新消息
     * event 事件名称
     * message 消息内容
     */
    fun sendNewMsg(event: String, message: String) {
        mSocket?.emit(event, message)
    }

    /**
     * 活动结束，关闭连接：
     */
    fun socketExit() {
        mSocket?.off()
        mSocket?.disconnect()
    }


}