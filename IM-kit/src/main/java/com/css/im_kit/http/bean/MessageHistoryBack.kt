package com.css.im_kit.http.bean

import com.css.im_kit.db.bean.UserInfo

data class MessageHistoryBack(
        var current_page: String,//当前页
        var data: List<MessageHistoryItem>,
        var last_page: String,
        val total: String
)

data class MessageHistoryItem(
        val message_id: String,
        val send_account: String,
        val receive_account: String,
        val shop_id: String,
        val message_type: Int,
        val read_status: Int,
        val send_status: Int,
        val created_time: String,
        val updated_time: String,
        val data_state: String,
        val message: String,
        val send_account_info: UserInfo,
        val receive_account_info: UserInfo
)

data class HttpMessage(
        val content: String,
        val extend: Extend,
        val m_id: String,
        val receive_account: String,
        val send_account: String,
        val source: Int,
        val time: Long,
        val type: Int
)

data class Extend(
        val shop_id: String
)