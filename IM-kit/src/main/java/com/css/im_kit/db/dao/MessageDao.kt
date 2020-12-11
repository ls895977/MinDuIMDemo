package com.css.im_kit.db.dao

import androidx.room.*
import com.css.im_kit.db.bean.Message

@Dao
interface MessageDao {
    @Query("SELECT  * FROM message WHERE conversationId = (:conversationId) ORDER BY messageId ASC")
    suspend fun getMessages(conversationId: String): List<Message>

    @Query("SELECT  * FROM message WHERE conversationId = (:conversationId)  ORDER BY targetId DESC LIMIT 1")
    suspend fun getLast(conversationId: String): Message

    @Query("SELECT  * FROM message WHERE conversationId = (:conversationId) AND isRead = :isRead")
    suspend fun getNoReadData(conversationId: String, isRead: Boolean): List<Message>

    @Insert
    suspend fun insert(users: Message)

    @Insert
    suspend fun insertDatas(users: List<Message>)

    @Delete
    suspend fun delete(user: Message)

    @Query("DELETE FROM message")
    suspend fun deleteAll()

    @Update
    suspend fun update(users: Message)

    @Update
    suspend fun update(users: List<Message>)

    /**
     * 设置消息已读
     */
    @Query("UPDATE message SET isRead = :isRead WHERE messageId IN (:messageIds)")
    suspend fun read(messageIds: List<String>, isRead: Boolean)
}