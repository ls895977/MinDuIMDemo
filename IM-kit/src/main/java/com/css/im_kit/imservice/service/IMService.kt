package com.css.im_kit.imservice.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.uiScope
import com.css.im_kit.imservice.JWebSClient
import com.css.im_kit.imservice.bean.DBMessageType
import com.css.im_kit.imservice.bean.ReceiveMessageBean
import com.css.im_kit.imservice.coom.ServiceType
import com.css.im_kit.imservice.interfacelinsterner.ServiceListener
import com.css.im_kit.imservice.interfacelinsterner.onLinkStatus
import com.css.im_kit.imservice.interfacelinsterner.onResultMessage
import com.css.im_kit.imservice.tool.CycleTimeUtils
import com.google.gson.Gson
import com.kongqw.network.monitor.NetworkMonitorManager
import com.kongqw.network.monitor.enums.NetworkState
import com.kongqw.network.monitor.interfaces.NetworkMonitor
import kotlinx.coroutines.launch
import org.java_websocket.enums.ReadyState
import java.net.URI
import java.util.concurrent.TimeUnit


/**
 * socket后台服务
 */
class IMService : Service(), ServiceListener {
    private var onResultMessage: onResultMessage? = null
    private var onLinkStatus: onLinkStatus? = null
    private var client: JWebSClient? = null
    private var serViceUrl = ""
    private val myBinder = IMServiceBinder()
    private var startStatus = false//判断是否是第一次启动好用于启动倒计时操作
    private var socketStatus = 0//当前socket状态变化0无状态1,2已链接3,4未链接
    var imServiceStatus = false//当前链接状态

    inner class IMServiceBinder : Binder() {
        val service: IMService
            get() = this@IMService
    }

    override fun onCreate() {
        super.onCreate()
        NetworkMonitorManager.getInstance().register(this)//网络监听
    }

    override fun onBind(intent: Intent): IBinder? {
        this.serViceUrl = intent.getStringExtra("serViceUrl").toString()
        startStatus = true
        initSocket()
        return myBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
//        CycleTimeUtils.canCelTimer()
        closeSocket()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        NetworkMonitorManager.getInstance().unregister(this)//取消网络监听
    }

    /**
     * 断开链接
     */
    private fun closeSocket() {
        try {
            if (null != client) {
                client?.closeBlocking()
                client?.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            client = null
        }
    }

    /**
     * 心跳中
     */
    private fun startTimeSocket() {

    }

    var runSocketNum = 0

    /**
     * 因网络原因导致socket断开循环链接
     */
    private fun runSocket() {
        CycleTimeUtils.startTimer(8, object : CycleTimeUtils.onBackTimer {
            override fun backRunTimer() {
                Log.e("aa", "-----------重连")
                initSocket()
            }
        })
    }

    /**
     * 链接5次自动取消关闭
     */
    private fun colsSocket5() {
        CycleTimeUtils.onDestroy()
    }


    // TODO 连接上WIFI或蜂窝网络的时候回调
    @NetworkMonitor(monitorFilter = [NetworkState.WIFI, NetworkState.CELLULAR])
    fun onNetWorkStateChange2(networkState: NetworkState) {
        if (serViceUrl.isNotEmpty() && socketStatus != 1 && socketStatus != 2) {
            initSocket()
        }
    }

    /**
     * 创建init
     */
    private fun initSocket() {
        ioScope.launch {
            socketRun()
        }
    }

    /**
     *链接socket
     */
    private fun socketRun() {
        if (client == null) {
            if (TextUtils.isEmpty(serViceUrl)) {
                return
            }
            try {
                val uri: URI = URI.create(serViceUrl)
                client = JWebSClient(uri)
                client?.setSocketListener(this)
                client?.connectBlocking(6, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                onLinkStatus?.onLinkedClose()
                socketStatus = 3
            }
        } else {
            try {
                client?.reconnect()
            } catch (e: InterruptedException) {
                e.printStackTrace()
                onLinkStatus?.onLinkedClose()
                socketStatus = 3
            }
//                if (client?.isOpen == false) {
//                    if (client?.readyState?.equals(ReadyState.NOT_YET_CONNECTED)!!) {
//                        try {
//                            client?.connectBlocking()
//                        } catch (e: Exception) {
//                            onLinkStatus?.onLinkedClose()
//                            socketStatus = 3
//                        }
//                    } else if (client?.readyState?.equals(ReadyState.CLOSING)!! || client?.readyState?.equals(ReadyState.CLOSED)!!) {
//                        try {
//                            client?.reconnectBlocking()
//                        } catch (e: InterruptedException) {
//                            e.printStackTrace()
//                            onLinkStatus?.onLinkedClose()
//                            socketStatus = 3
//                        }
//                    }
//                }
        }
    }

    /**
     * 发送新消息
     * event 事件名称
     * message 消息内容
     */
    fun sendNewMsg(message: String) {
        if (client?.readyState != ReadyState.OPEN) {
            onLinkStatus?.onLinkedClose()
            socketStatus = 3
            return
        }
        if (client != null && client?.isOpen!!) {
            try {
                client?.send(message)
            } catch (e: java.lang.Exception) {
                onLinkStatus?.onLinkedClose()
                socketStatus = 3
            }
        }
    }

//    var pongStats = true//心跳开关
//    /**
//     * 发送心跳
//     */
//    private fun sendPing() {
//        if (client?.readyState != ReadyState.OPEN) {
//            return
//        }
//        if (!pongStats) {
//            onLinkStatus?.onLinkedSuccess()
//            socketStatus = 3
////            CycleTimeUtils.canCelTimer()//执行心跳关闭
//            return
//        }
//        try {
//            ioScope.launch {
//                client?.sendPing()
//                pongStats = false
//            }
//        } catch (e: java.lang.Exception) {
//            onLinkStatus?.onLinkedSuccess()
//            socketStatus = 3
//        }
//    }

    /**
     * 重新链接
     * 必须建立在已链接基础上否则无效
     */
    fun retryIMService() {
        if (TextUtils.isEmpty(serViceUrl)) {
            return
        }
        runSocketNum=0
        socketRun()
    }

    /**
     * 消息反馈状态处理
     */
    override fun onBackSocketStatus(event: Int, msg: String) {
        when (event) {
            ServiceType.openMessageStats -> {//已链接
            }
            ServiceType.collectMessageStats -> {//链接收到消息
                runSocketNum = 0
                imServiceStatus = true
                val msgBean = Gson().fromJson(msg, ReceiveMessageBean::class.java)
                if (msgBean.m_id == "0" && msgBean.type != DBMessageType.REASSIGNCCUSTOMERSERVICE.value) {//通过数据反馈链接成功
                    if (socketStatus != 1 && socketStatus != 2) {
                        onLinkStatus?.onLinkedSuccess()
                        socketStatus = event
                    }
                } else {
                    onResultMessage?.onMessage(msg)
                }
                CycleTimeUtils.canCelTimer()//执行重连关闭
            }
            ServiceType.closeMessageStats, ServiceType.errorMessageStats -> {//链接关闭或者链接发生错误
                imServiceStatus = false
                //网络链接判断处理
                if (socketStatus != 3 && socketStatus != 4) {
                    uiScope.launch {
                        runSocket()//执行5次链接
                    }
                    onLinkStatus?.onLinkedClose()
                    socketStatus = event
                    Log.e("aa", "-----------链接关闭或者链接发生错误---")
                }
                if (runSocketNum == 3) {
                    onLinkStatus?.onLinkedClose()
                }
                runSocketNum++
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