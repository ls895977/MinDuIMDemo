package com.css.im_kit.socket.coom
/**
 * 状态码
 */
object SocketType {
    var openMessageStats = 1 //已链接
    var collectMessageStats = 2//链接收到消息
    var closeMessageStats = 3//链接关闭
    var errorMessageStats = 4//链接发生错误
}