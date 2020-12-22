package com.css.im_kit.imservice.bean

import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.bean.SendType
import com.css.im_kit.db.gson
import com.css.im_kit.utils.long13

data class ReceiveMessageBean(
        /**
         *code	string	是	状态 '20000'-成功 '40000'-失败
        m_id	string	是	消息ID
        type	int	是	消息类型-见备注
        source	int	是	消息来源-见备注
        receive_account	string	是	接收方账号
        send_account	string	是	发送方账号
        content	string	是	消息内容
        time	int	是	消息时间-时间戳
        extend.shop_id	int	是	聊天店铺ID
         */
        var code: Int,
        var m_id: String,
        var type: Int,
        var source: Int,
        var receive_account: String,
        var send_account: String,
        var content: String,
        var time: Long,
        var extend: HashMap<String, String>?

) {

    /**
    var m_id: String,//消息id
    var send_account: String,//发送账号
    var receive_account: String,//接收账号
    var shop_id: String,//店铺id
    var source: Int,//消息来源-见备注
    var message_type: Int,//消息类型
    var read_status: Boolean,//是否未读
    var send_status: Int,//是否发送成功
    var send_time: Long,//发送时间
    var receive_time: Long,//接收时间
    var message: String,//消息内容
    var extend: String//扩展消息
     */
    fun toDBMessage(): Message {
        return Message(
                m_id = m_id,
                send_account = send_account,
                receive_account = receive_account,
                shop_id = extend?.get("shop_id") ?: "",
                source = source,
                message_type = type,
                read_status = false,
                send_status = SendType.SUCCESS.text,
                send_time = time.long13(),
                receive_time = System.currentTimeMillis(),
                message = content,
                extend = gson.toJson(extend)
        )
    }

    fun toJsonString(): String {
        return gson.toJson(this)
    }
}