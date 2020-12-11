package com.css.im_kit.db.repository

import android.content.Context
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.dao.MessageDao
import com.css.im_kit.db.imDb

class MessageRepository {

    // 通过伴生对象实现单例模式
    companion object {
        private var dao: MessageDao? = null

        @Volatile
        private var INSTANCE: MessageRepository? = null
        fun build(context: Context): MessageRepository {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    dao = context.imDb().messageDao
                    instance = MessageRepository()
                }
                return instance
            }
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

        suspend fun delete(message: Message) {
            dao?.delete(message)
        }

        suspend fun deleteAll() {
            dao?.deleteAll()
        }
    }
}