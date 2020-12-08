package com.css.im_kit.db.dao

import androidx.room.*
import com.css.im_kit.db.bean.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM message")
    fun getAll(): Flow<List<Message>>

    @Query("SELECT * FROM message WHERE messageId IN (:message_ids)")
    fun loadAllByIds(message_ids: IntArray): Flow<List<Message>>

    @Query("SELECT * FROM message WHERE messageId = (:message_id)")
    fun loadById(message_id: String): Flow<List<Message>>

    @Insert
    suspend fun insertAll(users: Message)

    @Delete
    suspend fun delete(user: Message)

    @Update
    suspend fun updateUsers(users: Message)
}