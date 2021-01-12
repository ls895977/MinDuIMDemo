package com.css.im_kit.imservice.coom
/**
 * 状态码
 */
object ServiceType {
    var openMessageStats = 1 //已链接
    var collectMessageStats = 2//链接收到消息
    var closeMessageStats = 3//链接关闭
    var errorMessageStats = 4//链接发生错误
    var websocketPongStats = 5//收到Pong心跳
}