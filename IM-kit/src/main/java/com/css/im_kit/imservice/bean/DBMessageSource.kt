package com.css.im_kit.imservice.bean

import java.io.Serializable

/**
1	用户->客服
2	用户->用户
3	客服->用户
4	系统->用户
5	系统->客服
11	系统消息
12	互动消息
13	新增粉丝
14	订单消息
21	铭感词消息
 */
enum class DBMessageSource(val value: Int) : Serializable {
    USER2SERVICE(1),
    USER2USER(2),
    SERVICE2USER(3),
    SYSTEM2USER(4),
    SYSTEM2SERVICE(5),
    SYSTEM(11),
    INTERACTION(12),
    FANS(13),
    ORDER(14),
    INSCRIPTIONS(21),
}