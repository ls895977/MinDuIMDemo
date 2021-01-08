package com.css.im_kit.db.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.css.im_kit.db.gson
import com.css.im_kit.imservice.bean.SendMessageBean
import com.css.im_kit.utils.long10
import java.io.Serializable

/**
 * 消息
 */
@Entity
data class Message(
        var m_id: String,//消息id
        var send_account: String,//发送账号
        var receive_account: String,//接收账号
        var shop_id: String,//店铺id
        var source: Int,//消息来源-见备注
        var message_type: Int,//消息类型
        var read_status: Int,//是否未读
        var send_status: Int,//是否发送成功
        var send_time: Long,//发送时间
        var receive_time: Long,//接收时间
        var message: String,//消息内容
        var extend: String//扩展消息

) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id = 0//id

    /**
     *
    var m_id: String,
    var type: Int,
    var source: Int,
    var receive_account: String,
    var send_account: String,
    var content: String,
    var time: Long,
    var extend: String
     */
    fun toSendMessageBean(): SendMessageBean {
        val extend = gson.fromJson(extend,HashMap::class.java)
        return SendMessageBean(
                m_id,
                message_type,
                source,
                receive_account,
                send_account,
                message,
                send_time.long10(),
                extend as HashMap<String, Any>)
    }
}

enum class SendType(var text: Int) {
    SENDING(0),
    SUCCESS(1),
    FAIL(2)
}