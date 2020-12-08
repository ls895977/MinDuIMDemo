package com.css.im_kit.db.repository

import android.content.Context
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.dao.MessageDao
import com.css.im_kit.db.imDb
import com.css.im_kit.db.ioScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class MessageRepository(private val dao: MessageDao) {


    suspend fun getAll(listen: ((List<Message>) -> Unit)) {
        ioScope.launch {
            val task = async { dao.getAll() }
            val result = task.await()
            result.collect {
                listen(it)
            }
        }
    }

    suspend fun insert(message: Message) {
        return dao.insert(message)
    }

    /**
     * 获取最新消息新消息
     */
    suspend fun getLast(listen: ((Message) -> Unit)) {
        ioScope.launch {
            val task = async { dao.getLast() }
            val result = task.await()
            result.collect {
                listen(it)
            }
        }
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