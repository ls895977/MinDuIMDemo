package com.css.im_kit.imservice.bean

/**
1	用户->客服
2	用户->用户
3	客服->用户
4	系统->用户
5	系统->客服
 */
enum class DBMessageSource(val value: Int) {
    USER2SERVICE(1),
    USER2USER(2),
    SERVICE2USER(3),
    SYSTEM2USER(4),
    SYSTEM2SERVICE(5);
}