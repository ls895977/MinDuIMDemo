package com.css.im_kit.db.repository

import android.content.Context
import com.css.im_kit.db.bean.Conversation
import com.css.im_kit.db.dao.ConversationDao
import com.css.im_kit.db.imDb
import kotlinx.coroutines.flow.Flow

class ConversationRepository(private val conversationDao: ConversationDao) {

    suspend fun getAll(): Flow<List<Conversation>> {
        return conversationDao.getAll()
    }

    suspend fun insert(user: Conversation) {
        return conversationDao.insert(user)
    }

    suspend fun insert(user: List<Conversation>) {
        return conversationDao.insert(user)
    }

    suspend fun update(user: Conversation) {
        return conversationDao.update(user)
    }

    suspend fun delete(user: Conversation) {
        return conversationDao.delete(user)
    }

    // 通过伴生对象实现单例模式
    companion object {
        @Volatile
        private var INSTANCE: ConversationRepository? = null
        fun getInstance(context: Context): ConversationRepository {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = ConversationRepository(context.imDb().conversationDao)
                }
                return instance
            }
        }
    }
}