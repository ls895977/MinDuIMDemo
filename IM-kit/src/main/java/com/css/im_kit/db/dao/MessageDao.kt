package com.css.im_kit.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.css.im_kit.db.bean.Message

@Dao
interface MessageDao {
    @Query("SELECT * FROM message")
    suspend fun getAll(): LiveData<Array<Message>>

    @Query("SELECT * FROM message WHERE message_id IN (:message_ids)")
    suspend fun loadAllByIds(message_ids: IntArray): LiveData<Array<Message>>

    @Query("SELECT * FROM message WHERE message_id = (:message_id)")
    suspend fun loadById(message_id: String): LiveData<Array<Message>>

    @Insert
    suspend fun insertAll(vararg users: Message)

    @Delete
    suspend fun delete(vararg user: Message)

    @Update
    suspend fun updateUsers(vararg users: Message)
}