package com.css.im_kit

import android.app.Application
import com.css.im_kit.db.repository.MessageRepository
import com.css.im_kit.db.repository.UserInfoRepository
import com.css.im_kit.imservice.MessageServiceUtils
import com.css.im_kit.imservice.`interface`.onLinkStatus
import com.css.im_kit.manager.IMMessageManager
import com.kongqw.network.monitor.NetworkMonitorManager

object IMManager {
    fun build(context: Application, app_id: String, app_secret: String) {
        MessageRepository.build(context)
        UserInfoRepository.build(context)
        NetworkMonitorManager.getInstance().init(context)
        MessageServiceUtils.init(context)
        this.app_id = app_id
        this.app_secret = app_secret
    }

    var userID: String? = null
    var app_id: String? = null
    var app_secret: String? = null

    /**
     * 连接聊天socket
     * socketUrl 聊天服务地址
     * token 登录凭证
     * onLinkStatus 链接状态反馈
     */
    fun connect(socketUrl: String, token: String, userID: String, onLinkStatus: onLinkStatus) {
        this.userID = userID
        MessageServiceUtils.initService("$socketUrl?account=$token", onLinkStatus)
        //开启socket监听
        IMMessageManager.openSocketListener()
    }

    /**
     * 重新链接
     * 必须建立在已链接基础上否则无效
     */
    fun retryService() {
        MessageServiceUtils.retryService()
    }


    /**
     * 断开客户端联系
     */
    fun closeConnect() {
        MessageServiceUtils.closeConnect()
    }
}