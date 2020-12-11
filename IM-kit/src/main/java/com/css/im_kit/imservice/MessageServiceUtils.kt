package com.css.im_kit.imservice

import android.app.Application
import android.text.TextUtils
import android.util.Log
import com.css.im_kit.IMManager
import com.css.im_kit.imservice.`interface`.ServiceListener
import com.css.im_kit.imservice.`interface`.onLinkStatus
import com.css.im_kit.imservice.`interface`.onResultMessage
import com.css.im_kit.imservice.coom.ServiceType
import com.kongqw.network.monitor.enums.NetworkState
import com.kongqw.network.monitor.interfaces.NetworkMonitor
import com.kongqw.network.monitor.util.NetworkStateUtils
import java.net.URI

object MessageServiceUtils {
    private var client: JWebSClient? = null
    private var SerViceUrl = ""
    private var retryIndex = 0//关闭重连控制
    private var closeConnectStatus = false//是否是手动关闭
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
//        NetworkMonitorManager.getInstance().register(this)//网络监听
//        NetworkMonitorManager.getInstance().unregister(this)//网络监听
        this.onLinkStatus = onLinkStatus
        this.SerViceUrl = CHAT_SERVER_URL.toString()
        try {
            val uri: URI = URI.create(CHAT_SERVER_URL)
            client = JWebSClient(uri)
            listeners()
            client?.connectBlocking()
        } catch (e: Exception) {
        }

    }

    /**
     * CHAT_SERVER_URL 聊天服务地址
     */
    fun initService(CHAT_SERVER_URL: String?) {
//        NetworkMonitorManager.getInstance().register(this)//网络监听
//        NetworkMonitorManager.getInstance().unregister(this)//网络监听
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
        client?.setSocketListener(object : ServiceListener {
            override fun onBackSocketStatus(event: Int, msg: String) {
                when (event) {
                    ServiceType.openMessageStats -> {//已链接
                        retryIndex = 0//重置为零
                        onLinkStatus?.onLinkedSuccess()
                    }
                    ServiceType.collectMessageStats -> {//链接收到消息
                        retryIndex = 0//重置为零
                        onResultMessage?.onMessage(msg)
                    }
                    ServiceType.closeMessageStats -> {//链接关闭
                        //网络链接判断处理
                        val netStatus: Boolean = NetworkStateUtils.hasNetworkCapability(mApplication!!)
                        if (retryIndex < 5 && !closeConnectStatus && netStatus) {//链接重试五次后不在重连
                            retryService()
                            retryIndex++
                        } else {//反馈最终链接还是断开问题
                            onLinkStatus?.onLinkedClose()
                        }
                    }
                    ServiceType.errorMessageStats -> {//链接发生错误
                        retryIndex = 0//重置为零
                        onLinkStatus?.onLinkedClose()
                    }
                }

            }
        })
    }

    /**
     * 网络断开时回调
     */
    @NetworkMonitor(monitorFilter = [NetworkState.NONE])
    fun onNetWorkStateChangeNONE(networkState: NetworkState) {
        Log.e("aa", "-----------网络断开时回调")
    }

    @NetworkMonitor(monitorFilter = [NetworkState.WIFI])
    fun onNetWorkStateChange1(networkState: NetworkState) {
        Log.e("aa", "-----------WIFI连接上的时候回调")
    }

    @NetworkMonitor(monitorFilter = [NetworkState.WIFI, NetworkState.CELLULAR])
    fun onNetWorkStateChange2(networkState: NetworkState) {
        Log.e("aa", "-----------连接上WIFI或蜂窝网络的时候回调")
    }
}