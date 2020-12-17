package com.css.im_kit.imservice.service
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.text.TextUtils
import com.css.im_kit.imservice.JWebSClient
import com.css.im_kit.imservice.interfacelinsterner.ServiceListener
import com.css.im_kit.imservice.interfacelinsterner.onLinkStatus
import com.css.im_kit.imservice.interfacelinsterner.onResultMessage
import com.css.im_kit.imservice.coom.ServiceType
import java.net.URI
/**
 * socket后台服务
 */
class IMService : Service(),ServiceListener {
    private var onResultMessage: onResultMessage? = null
    private var onLinkStatus: onLinkStatus? = null
    private var client: JWebSClient? = null
    private var serViceUrl = ""
    private val myBinder = IMServiceBinder()

    inner class IMServiceBinder : Binder() {
        val service: IMService
            get() = this@IMService
    }

    override fun onBind(intent: Intent): IBinder? {
        this.serViceUrl = intent.getStringExtra("serViceUrl").toString()
        initSocket()
        return myBinder
    }

    override fun onUnbind(intent: Intent): Boolean {

        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     * 创建init
     */
    private fun initSocket() {
        Thread {
            kotlin.run {
                val uri: URI = URI.create(serViceUrl)
                client = JWebSClient(uri)
                client?.setSocketListener(this)
                client?.connectBlocking()
            }
        }.start()
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
     * 必须建立在已链接基础上否则无效
     */
    fun retryIMService() {
        if (TextUtils.isEmpty(serViceUrl)) {
            return
        }
        initSocket()
    }

    /**
     * 断开客户端联系
     */
    fun imServiceClose() {
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

    /**
     * 消息反馈状态处理
     */
    override fun onBackSocketStatus(event: Int, msg: String) {
        when (event) {
            ServiceType.openMessageStats -> {//已链接
                onLinkStatus?.onLinkedSuccess()
            }
            ServiceType.collectMessageStats -> {//链接收到消息
//                MessageServiceUtils.retryIndex = 0//重置为零
                onResultMessage?.onMessage(msg)
            }
            ServiceType.closeMessageStats -> {//链接关闭
                //网络链接判断处理
//                val netStatus: Boolean = NetworkStateUtils.hasNetworkCapability(this)
//                if (MessageServiceUtils.retryIndex < 5 && !MessageServiceUtils.closeConnectStatus && netStatus) {//链接重试五次后不在重连
//                    MessageServiceUtils.retryService()
//                    MessageServiceUtils.retryIndex++
//                } else {//反馈最终链接还是断开问题
                onLinkStatus?.onLinkedClose()
//                }
            }
            ServiceType.errorMessageStats -> {//链接发生错误
                onLinkStatus?.onLinkedClose()
            }
        }
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
}