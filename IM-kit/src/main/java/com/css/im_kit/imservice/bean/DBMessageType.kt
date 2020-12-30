package com.css.im_kit.imservice.bean

/**
1	文本消息
2	富文本消息
3	图片消息
4	视频消息
5	语音消息
100	客户端收到消息回执
101	服务端收到消息回执
 */
enum class DBMessageType(val value: Int) {
    TEXT(1),
    RICH(2),
    IMAGE(3),
    VIDEO(4),
    VOICE(5),
    CLIENTRECEIPT(100),
    SERVERRECEIPT(101),
    WELCOME (102),//客服欢迎消息
    NONBUSINESSHOURS(103)//非营业时间回复消息
}