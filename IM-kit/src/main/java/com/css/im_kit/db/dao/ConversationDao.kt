package com.css.im_kit.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.css.im_kit.db.bean.Conversation
import com.css.im_kit.db.bean.Message

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversation")
    suspend fun getAll(): LiveData<Array<Conversation>>

    @Query("SELECT * FROM conversation WHERE id IN (:ids)")
    suspend fun loadAllByIds(ids: IntArray): LiveData<Array<Conversation>>

    @Query("SELECT * FROM conversation WHERE id = (:id)")
    suspend fun loadById(id: String): LiveData<Array<Conversation>>

    @Insert
    suspend fun insertAll(vararg users: Message)

    @Delete
    suspend fun delete(vararg user: Message)

    @Update
    suspend fun updateUsers(vararg users: Message)
}