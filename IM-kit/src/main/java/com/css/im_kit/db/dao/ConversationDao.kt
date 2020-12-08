package com.css.im_kit.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.css.im_kit.db.bean.Conversation
import com.css.im_kit.db.bean.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversation")
    fun getAll(): Flow<List<Conversation>>

    @Query("SELECT * FROM conversation WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): Flow<List<Conversation>>

    @Query("SELECT * FROM conversation WHERE id = (:id)")
    fun loadById(id: String): Flow<List<Conversation>>

    @Insert
    suspend fun insertAll(users: Conversation)

    @Delete
    suspend fun delete(user: Conversation)

    @Update
    suspend fun updateUsers(users: Conversation)
}