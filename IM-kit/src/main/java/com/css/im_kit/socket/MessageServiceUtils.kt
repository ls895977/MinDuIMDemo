package com.css.im_kit.socket

import android.text.TextUtils
import android.util.Log
import com.css.im_kit.socket.`interface`.SocketListener
import com.css.im_kit.socket.`interface`.onLinkStatus
import com.css.im_kit.socket.`interface`.onResultMessage
import com.css.im_kit.socket.coom.SocketType
import java.net.URI

object MessageServiceUtils {
    private var client: JWebSClient? = null
    private var SerViceUrl = ""
    private var retryIndex = 0//关闭重连控制
    private var closeConnectStatus = false//是否是手动关闭
    private var onResultMessage: onResultMessage? = null
    private var onLinkStatus: onLinkStatus? = null

    /**
     * CHAT_SERVER_URL 聊天服务地址
     * onLinkStatus 链接状态反馈
     */
    fun initService(CHAT_SERVER_URL: String?, onLinkStatus: onLinkStatus?) {
        this.onLinkStatus = onLinkStatus
        this.SerViceUrl = CHAT_SERVER_URL.toString()
        val uri: URI = URI.create(CHAT_SERVER_URL)
        client = JWebSClient(uri)
        listeners()
        client?.connectBlocking()
    }

    /**
     * CHAT_SERVER_URL 聊天服务地址
     */
    fun initService(CHAT_SERVER_URL: String?) {
        this.SerViceUrl = CHAT_SERVER_URL.toString()
        val uri: URI = URI.create(CHAT_SERVER_URL)
        client = JWebSClient(uri)
        listeners()
        client?.connectBlocking()
    }

    /**
     * 重新链接
     * 必须建立在已链接基础上否则无效
     */
    fun retryService() {
        if (TextUtils.isEmpty(SerViceUrl)) {
            return
        }
        closeConnectStatus = false
        val uri: URI = URI.create(SerViceUrl)
        client = JWebSClient(uri)
        listeners()
        client?.connectBlocking()
    }

    /**
     * 消息回馈监听
     */
    fun setOnResultMessage(onResultMessage: onResultMessage) {
        this.onResultMessage = onResultMessage
    }

    /**
     * 链接状态监听
     * 此回调会导致第一次链接无反馈状态
     */
    fun setOnLinkStatus(onLinkStatus: onLinkStatus) {
        this.onLinkStatus = onLinkStatus
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
     * 断开客户端联系
     */
    fun closeConnect() {
        try {
            if (null != client) {
                closeConnectStatus = true
                client?.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            client = null
        }
    }

    /**
     * 消息反馈状态处理
     */
    private fun listeners() {
        client?.setSocketListener(object : SocketListener {
            override fun onBackSocketStatus(event: Int, msg: String) {
                when (event) {
                    SocketType.openMessageStats -> {//已链接
                        retryIndex = 0//重置为零
                        onLinkStatus?.onLinkedSuccess()
                    }
                    SocketType.collectMessageStats -> {//链接收到消息
                        retryIndex = 0//重置为零
                        onResultMessage?.onMessage(msg)
                    }
                    SocketType.closeMessageStats -> {//链接关闭
                        if (retryIndex < 5 && !closeConnectStatus) {//链接重试五次后不在重连
                            retryService()
                            retryIndex++
                        } else {//反馈最终链接还是断开问题
                            onLinkStatus?.onLinkedClose()
                        }
                    }
                    SocketType.errorMessageStats -> {//链接发生错误
                        retryIndex = 0//重置为零
                        onLinkStatus?.onLinkedClose()
                    }
                }

            }
        })
    }


}