package com.css.im_kit.imservice

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.css.im_kit.imservice.interfacelinsterner.onLinkStatus
import com.css.im_kit.imservice.interfacelinsterner.onResultMessage
import com.css.im_kit.imservice.service.IMService
import com.css.im_kit.imservice.service.IMService.IMServiceBinder
import com.kongqw.network.monitor.NetworkMonitorManager

object MessageServiceUtils {
    private var onResultMessage: onResultMessage? = null
    private var onLinkStatus: onLinkStatus? = null
    private var mApplication: Application? = null
    fun init(application: Application) {
        mApplication = application
    }

    /**
     * CHAT_SERVER_URL 聊天服务地址
     * onLinkStatus 链接状态反馈
     */
    fun initService(CHAT_SERVER_URL: String?, onLinkStatus: onLinkStatus?) {
        NetworkMonitorManager.getInstance().register(this)//网络监听
        this.onLinkStatus = onLinkStatus
        val intent = Intent(mApplication, IMService::class.java)
        intent.putExtra("serViceUrl", CHAT_SERVER_URL)
        mApplication?.bindService(intent, imServiceConn, Context.BIND_AUTO_CREATE)
    }

    /**
     * 重新链接
     * 必须建立在已链接基础上否则无效
     */
    fun retryService() {

    }

    /**
     * 断开客户端联系
     */
    fun closeConnect() {
        myBindService?.imServiceClose()
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
        myBindService?.sendNewMsg(message)
    }

    private var myBindService: IMService? = null
    private val imServiceConn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as IMServiceBinder
            myBindService = binder.service
            myBindService?.setOnLinkStatus(object : onLinkStatus {
                override fun onLinkedSuccess() {
                    onLinkStatus?.onLinkedSuccess()
                }

                override fun onLinkedClose() {
                    onLinkStatus?.onLinkedClose()
                }
            })
            myBindService?.setOnResultMessage(object : onResultMessage {
                override fun onMessage(context: String) {
                    onResultMessage?.onMessage(context)
                }
            })
        }

        override fun onServiceDisconnected(p0: ComponentName?) {

        }
    }

    /**
     * 绑定服务
     */
    fun startBing() {

    }

    /**
     * 解绑
     */
    fun stopBing() {
        try {
            mApplication?.unbindService(imServiceConn)
        } catch (e: Exception) {
        }
    }

}