package com.css.im_kit.utils

import java.security.MessageDigest
import java.util.*

fun String.md5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.hex()
}

fun ByteArray.hex(): String {
    return joinToString("") { "%02X".format(it) }
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
