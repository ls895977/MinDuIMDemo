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
    @Synchronized
    suspend fun getMessage(shop_id: String): List<Message> {
        return dao?.getMessages(shop_id) ?: arrayListOf()
    }

    /**
     * 消息分页
     * page 0  开始
     *
     */
    @Synchronized
    suspend fun getMessage(shop_id: String, lastItemTime: Long, pageSize: Int): MutableList<Message> {
        return dao?.getMessages(shop_id, lastItemTime = lastItemTime, pageSize = pageSize)
                ?: arrayListOf()
    }

    @Synchronized
    suspend fun getMessage4Account(chat_account: String, lastItemTime: Long, pageSize: Int): MutableList<Message> {
        return dao?.getMessages4Account(chat_account = chat_account, lastItemTime = lastItemTime, pageSize = pageSize)
                ?: arrayListOf()
    }

    @Synchronized
    suspend fun delete4account(chat_account: String) {
        dao?.delete4account(chat_account = chat_account)
    }

    @Synchronized
    suspend fun delete4ShopId(shop_id: String) {
        dao?.delete4ShopId(shop_id = shop_id)
    }


    /**
     * 获取消息
     * messageId 消息id
     */
    @Synchronized
    suspend fun getMessage4messageId(messageId: String): Message? {
        return dao?.getMessage4messageId(messageId)
    }

    /**
     * 修改消息内容
     * content = 消息内容
     * messageId 消息id
     */
    @Synchronized
    suspend fun changeMessageContent(messageId: String, content: String) {
        dao?.changeMessageContent(content, messageId)
    }

    /**
     * 修改单条消息的发送状态
     *
    SENDING(0),
    SUCCESS(1),
    FAIL(2)
     */
    @Synchronized
    suspend fun changeMessageSendType(sendType: SendType, messageId: String) {
        dao?.changeMessageSendType(sendType.text, arrayListOf(messageId))
    }

    @Synchronized
    suspend fun insert(message: Message): Boolean {
        val data = dao?.getMessage4MessageId(message.m_id)
        if (data.isNullOrEmpty()) {
            dao?.insert(message)
            return true
        } else {
            return false
        }
    }

    @Synchronized
    suspend fun insertDatas(messages: List<Message>) {
        messages.filter {
            dao?.getMessage4MessageId2(it.m_id).isNullOrEmpty()
        }.let {
            dao?.insertDatas(it)
        }
    }

    /**
     * 获取最新消息新消息
     * conversationId ： 会话id
     */
    @Synchronized
    suspend fun getLast(shop_id: String?): Message? {
        return if (shop_id.isNullOrEmpty()) {
            dao?.getLast()
        } else {
            dao?.getLast(shop_id)
        }
    }

    /**
     * 拉取历史消息
     * conversationId ： 会话id
     */
    @Synchronized
    suspend fun getFirst(shop_id: String): Message? {
        return dao?.getFirst(shop_id)
    }

    @Synchronized
    suspend fun getLast(): Message? {
        return getLast(null)
    }

    @Synchronized
    suspend fun update(message: Message) {
        dao?.update(message)
    }

    @Synchronized
    suspend fun update(messages: List<Message>) {
        dao?.update(messages)
    }

    /**
     * 设置消息已读
     */
    @Synchronized
    suspend fun read(chat_account: String) {
        dao?.read(chat_account)
    }

    /**
     * 设置消息已读
     */
    @Synchronized
    suspend fun read(mids: List<String>) {
        dao?.read(mids)
    }

    @Synchronized
    suspend fun getNoReadData(shop_id: String, send_account: String): Int {
        return dao?.getNoReadData(shop_id, 0, send_account = send_account)?.size ?: 0
    }

    @Synchronized
    suspend fun delete(message: Message) {
        dao?.delete(message)
    }

    @Synchronized
    suspend fun delete(messageId: String) {
        dao?.delete(messageId)
    }

    @Synchronized
    suspend fun deleteAll() {
        dao?.deleteAll()
    }
}