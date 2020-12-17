package com.css.im_kit.imservice.service
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import com.css.im_kit.db.uiScope
import com.css.im_kit.imservice.JWebSClient
import com.css.im_kit.imservice.bean.ReceiveMessageBean
import com.css.im_kit.imservice.interfacelinsterner.ServiceListener
import com.css.im_kit.imservice.interfacelinsterner.onLinkStatus
import com.css.im_kit.imservice.interfacelinsterner.onResultMessage
import com.css.im_kit.imservice.coom.ServiceType
import com.css.im_kit.imservice.tool.CycleTimeUtils
import com.google.gson.Gson
import com.kongqw.network.monitor.NetworkMonitorManager
import com.kongqw.network.monitor.enums.NetworkState
import com.kongqw.network.monitor.interfaces.NetworkMonitor
import com.kongqw.network.monitor.util.NetworkStateUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.URI
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
    private var retryIndex = 0//重连次数控制
    private var socketStatus = false//当前socket状态变化

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
//        startTimeSocket()
        return myBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        CycleTimeUtils.canCelTimer()
        try {
            if (null != client) {
                client?.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            client = null
        }
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        NetworkMonitorManager.getInstance().unregister(this)//取消网络监听
    }

    /**
     * 倒计时重连socket确宝链接状态
     */
    private fun startTimeSocket() {
        CycleTimeUtils.startTimer(6, object : CycleTimeUtils.onBackTimer {
            override fun backRunTimer() {
                socketRun()
            }
        })
    }

    /**
     * 创建init
     */
    private fun initSocket() {
        Thread {
            kotlin.run {
                socketRun()
            }
        }.start()
    }

    //TODO 网络断开时回调
    @NetworkMonitor(monitorFilter = [NetworkState.NONE])
    fun onNetWorkStateChangeNONE(networkState: NetworkState) {

    }

    // TODO 连接上WIFI或蜂窝网络的时候回调
    @NetworkMonitor(monitorFilter = [NetworkState.WIFI, NetworkState.CELLULAR])
    fun onNetWorkStateChange2(networkState: NetworkState) {

    }

    /**
     *链接socket
     */
    private fun socketRun() {
        val uri: URI = URI.create(serViceUrl)
        client = JWebSClient(uri)
        client?.connectionLostTimeout = 6
        client?.setSocketListener(this)
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
     * 必须建立在已链接基础上否则无效
     */
    fun retryIMService() {
        if (TextUtils.isEmpty(serViceUrl)) {
            return
        }
        initSocket()
    }

    /**
     * 消息反馈状态处理
     */
    override fun onBackSocketStatus(event: Int, msg: String) {
        when (event) {
            ServiceType.openMessageStats -> {//已链接
                retryIndex = 0//重置链接次数为零
//                uiScope.launch {
//                    async {
//                        if (startStatus) {//判断第一次链接成功执行倒计时刷新
//
//                            startStatus = false
//                        }
//                    }
//                }
//                onLinkStatus?.onLinkedSuccess()//webSocket反馈链接成功
            }
            ServiceType.collectMessageStats -> {//链接收到消息
                val msgBean = Gson().fromJson(msg, ReceiveMessageBean::class.java)
                if (msgBean.m_id == "0") {//通过数据反馈链接成功
                    onLinkStatus?.onLinkedSuccess()
                } else {
                    onResultMessage?.onMessage(msg)
                }
            }
            ServiceType.closeMessageStats -> {//链接关闭
                if (socketStatus) {
                    socketStatus = false
                }
                Log.e("aa", "---------------------链接关闭")
                //网络链接判断处理
                val netStatus: Boolean = NetworkStateUtils.hasNetworkCapability(this)
                if (retryIndex < 5 && netStatus) {//链接重试五次后不在重连
                    retryIMService()
                    retryIndex++
                } else {//反馈最终链接还是断开问题
                    onLinkStatus?.onLinkedClose()
                }
            }
            ServiceType.errorMessageStats -> {//链接发生错误
                if (socketStatus) {
                    socketStatus = false
                }
                Log.e("aa", "---------------------链接发生错误")
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