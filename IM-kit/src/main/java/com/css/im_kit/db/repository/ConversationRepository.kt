package com.css.im_kit.db.repository

import android.content.Context
import com.css.im_kit.db.bean.Conversation
import com.css.im_kit.db.dao.ConversationDao
import com.css.im_kit.db.imDb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class ConversationRepository {
    // 通过伴生对象实现单例模式
    companion object {
        private var conversationDao: ConversationDao? = null

        @Volatile
        private var INSTANCE: ConversationRepository? = null
        fun build(context: Context): ConversationRepository {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    this.conversationDao = context.imDb().conversationDao
                    instance = ConversationRepository()
                }
                return instance
            }
        }

        fun getAll(): Flow<List<Conversation>> {
            return conversationDao?.getAll() ?: flow { arrayListOf<Conversation>() }
        }

        suspend fun findConversation(conversationId:String): Conversation? {
            return conversationDao?.loadById(conversationId)
        }

        suspend fun insert(user: Conversation) {
            conversationDao?.insert(user)
        }

        suspend fun insert(user: List<Conversation>) {
            conversationDao?.insert(user)
        }

        suspend fun update(user: Conversation) {
            conversationDao?.update(user)
        }

        suspend fun insertOrUpdate(conversations: ArrayList<Conversation>): Boolean {
            try {
                val list = conversationDao?.getAll()
                list?.collect { dbConversations ->
                    val updateList = arrayListOf<Conversation>()
                    //获取数据库内有的数据conversationId一样
                    dbConversations.forEach { dbConversation ->
                        conversations.forEach { initConversation ->
                            if (initConversation.conversationId == dbConversation.conversationId) {
                                initConversation.id = dbConversation.id
                                updateList.add(initConversation)
                            }
                        }
                    }
                    conversationDao?.update(updateList)
                    updateList.forEach {
                        conversations.remove(it)
                    }
                    conversationDao?.insert(conversations)
                }
                return true
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }

        suspend fun delete(user: Conversation) {
            conversationDao?.delete(user)
        }

    }
}