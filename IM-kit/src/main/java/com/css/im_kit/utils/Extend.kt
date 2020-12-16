package com.css.im_kit.utils

import java.security.MessageDigest

fun String.md5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.hex().substring(8,24)
}

fun ByteArray.hex(): String {
    return joinToString("") {"%02X".format(it)}
}