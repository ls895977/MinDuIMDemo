package com.css.im_kit.db.repository

import android.content.Context
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.bean.SendType
import com.css.im_kit.db.dao.MessageDao
import com.css.im_kit.db.imDb

object MessageRepository {

    private var dao: MessageDao? = null

    fun build(context: Context) {
        dao = context.imDb().messageDao
    }

    /**
     * 获取消息列表
     */
    suspend fun getMessage(shop_id: String): List<Message> {
        return dao?.getMessages(shop_id) ?: arrayListOf()
    }

    /**
     * 获取消息
     * messageId 消息id
     */
    suspend fun getMessage4messageId(messageId: String): Message? {
        return dao?.getMessage4messageId(messageId)
    }

    /**
     * 修改单条消息的发送状态
     *
    SENDING(0),
    SUCCESS(1),
    FAIL(2)
     */
    suspend fun changeMessageSendType(sendType: SendType, messageId: String) {
        dao?.changeMessageSendType(sendType.text, arrayListOf(messageId))
    }

    suspend fun insert(message: Message) {
        dao?.insert(message)
    }

    suspend fun insertDatas(messages: List<Message>) {
        dao?.insertDatas(messages)
    }

    /**
     * 获取最新消息新消息
     * conversationId ： 会话id
     */
    suspend fun getLast(shop_id: String?): Message? {
        return if (shop_id.isNullOrEmpty()) {
            dao?.getLast()
        } else {
            dao?.getLast(shop_id)
        }
    }

    suspend fun getLast(): Message? {
        return getLast(null)
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

    suspend fun getNoReadData(shop_id: String): Int {
        return dao?.getNoReadData(shop_id, false)?.size ?: 0
    }

    suspend fun delete(message: Message) {
        dao?.delete(message)
    }

    suspend fun deleteAll() {
        dao?.deleteAll()
    }
}