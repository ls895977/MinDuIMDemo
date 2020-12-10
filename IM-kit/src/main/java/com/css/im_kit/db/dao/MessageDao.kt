package com.css.im_kit.db.dao

import androidx.room.*
import com.css.im_kit.db.bean.Message

@Dao
interface MessageDao {
    @Query("SELECT  * FROM message WHERE conversationId = (:conversationId) ORDER BY messageId")
    suspend fun getMessages(conversationId: String): List<Message>

    @Query("SELECT  * FROM message WHERE conversationId = (:conversationId) ORDER BY messageId DESC LIMIT 1")
    suspend fun getLast(conversationId: String): Message

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
}