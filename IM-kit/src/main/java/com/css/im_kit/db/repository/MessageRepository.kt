package com.css.im_kit.db.repository

import android.content.Context
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.dao.MessageDao
import com.css.im_kit.db.imDb
import kotlinx.coroutines.flow.Flow

data class MessageRepository(private val dao: MessageDao) {


    fun getAll(): Flow<List<Message>> {
        return dao.getAll()
    }

    suspend fun insert(message: Message) {
        return dao.insert(message)
    }

    suspend fun insertDatas(messages: List<Message>) {
        return dao.insertDatas(messages)
    }

    /**
     * 获取最新消息新消息
     */
    suspend fun getLast(userId: String): Message? {
        return dao.getLast(userId)
    }

    suspend fun update(message: Message) {
        return dao.update(message)
    }

    suspend fun delete(message: Message) {
        return dao.delete(message)
    }

    suspend fun deleteAll() {
        return dao.deleteAll()
    }

    // 通过伴生对象实现单例模式
    companion object {
        @Volatile
        private var INSTANCE: MessageRepository? = null
        fun getInstance(context: Context): MessageRepository {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = MessageRepository(context.imDb().messageDao)
                }
                return instance
            }
        }
    }
}