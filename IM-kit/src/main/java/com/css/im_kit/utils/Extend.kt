package com.css.im_kit.utils

import android.util.Log
import android.widget.Toast
import com.css.im_kit.IMManager
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.bean.SendType
import com.css.im_kit.db.gson
import com.css.im_kit.http.bean.HttpMessage
import com.css.im_kit.http.bean.MessageHistoryItem
import java.security.MessageDigest
import java.util.*

fun String.md5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.hex()
}

fun ByteArray.hex(): String {
    return joinToString("") { "%02X".format(it) }
}

fun String.log() {
    Log.e("SGIM", this)
}

fun String.toast() {
    Toast.makeText(IMManager.context, this, Toast.LENGTH_SHORT).show()
}


/**
 * 生成签名. 注意，若含有sign_type字段，必须和signType参数保持一致。
 *
 * @param data 待签名数据
 * @param key API密钥
 * @param signType 签名方式
 * @return 签名
 */
@Throws(Exception::class)
fun Map<String, String>.generateSignature(key: String): String {
    val keySet = this.keys
    val keyArray = keySet.toTypedArray()
    Arrays.sort(keyArray)
    val sb = StringBuilder()
    for (k in keyArray) {
        if ((this[k] ?: error("")).trim { it <= ' ' }.isNotEmpty()) // 参数值为空，则不参与签名
            sb.append(k).append("=").append((this[k] ?: error("")).trim { it <= ' ' }).append("&")
    }
    sb.append("key=").append(key)
    return sb.toString().md5().toUpperCase()
}

/**
 * 获取文件扩展名
 *
 * @param filePath
 * @return
 */
fun getExtendS(filePath: String): String? {
    var endexFix = ".jpg"
    val strSS = filePath.split("\\.").toTypedArray()
    endexFix = if (strSS.size > 1) {
        "." + strSS[strSS.size - 1]
    } else {
        ".jpg"
    }
    return endexFix
}

/**
 *      var m_id: String,//消息id
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
fun MessageHistoryItem.toMessage(): Message {
    val message = gson.fromJson(this.message, HttpMessage::class.java)
    return Message(
            m_id = this.message_id,
            send_account = message.send_account,
            receive_account = message.receive_account,
            shop_id = this.shop_id,
            source = message.source,
            message_type = message.type,
            read_status = this.read_status,
            send_status = SendType.SUCCESS.text,
            send_time = message.time.long13(),
            receive_time = message.time.long13(),
            message = message.content,
            extend = gson.toJson(message.extend)
    )
}

fun Long.long10(): Long {
    return if (this.toString().length == 13)
        this.div(1000)
    else
        this
}

fun Long.long13(): Long {
    return if (this.toString().length == 10)
        this.times(1000)
    else
        this
}

fun String.hasHttp(): String {
    return if (!this.contains("http")) {
        IMManager.getImageBaseUrl() + this
    } else {
        this
    }
}
