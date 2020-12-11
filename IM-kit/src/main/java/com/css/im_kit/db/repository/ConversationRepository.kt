package com.css.im_kit.db.repository

import android.content.Context
import com.css.im_kit.db.bean.Conversation
import com.css.im_kit.db.dao.ConversationDao
import com.css.im_kit.db.imDb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

object ConversationRepository {
    private var conversationDao: ConversationDao? = null

    fun build(context: Context) {
        this.conversationDao = context.imDb().conversationDao
    }

    fun getAll(): Flow<List<Conversation>> {
        return conversationDao?.getAll() ?: flow { arrayListOf<Conversation>() }
    }

    suspend fun findConversation(conversationId: String): Conversation? {
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