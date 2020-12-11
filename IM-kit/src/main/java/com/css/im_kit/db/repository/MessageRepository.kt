package com.css.im_kit.db.repository

import android.content.Context
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.dao.MessageDao
import com.css.im_kit.db.imDb

object MessageRepository {

    private var dao: MessageDao? = null

    fun build(context: Context) {
        dao = context.imDb().messageDao
    }

    fun setOnResultMessage() {

    }

    /**
     * 获取消息列表
     */
    suspend fun getMessage(conversationId: String): List<Message> {
        return dao?.getMessages(conversationId) ?: arrayListOf()
    }

    suspend fun insert(message: Message) {
        dao?.insert(message)
    }

    suspend fun insertDatas(messages: List<Message>) {
        dao?.insertDatas(messages)
    }

    /**
     * 获取最新消息新消息
     */
    suspend fun getLast(conversationId: String): Message? {
        return dao?.getLast(conversationId)
    }

    suspend fun update(message: Message) {
        dao?.update(message)
    }

    suspend fun update(messages: List<Message>) {
        dao?.update(messages)
    }

    /**
     * 设置消息已读
     */
    suspend fun read(messageIds: List<String>) {
        dao?.read(messageIds, true)
    }

    suspend fun getNoReadData(conversationId: String): Int {
        return dao?.getNoReadData(conversationId, false)?.size ?: 0
    }

    suspend fun delete(message: Message) {
        dao?.delete(message)
    }

    suspend fun deleteAll() {
        dao?.deleteAll()
    }
}