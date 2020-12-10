package com.css.im_kit.db.dao

import androidx.room.*
import com.css.im_kit.db.bean.Conversation
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
    suspend fun insert(user: Conversation)

    @Insert
    suspend fun insert(user: List<Conversation>)

    @Insert
    suspend fun insertAll(users: List<Conversation>)

    @Delete
    suspend fun delete(user: Conversation)

    @Query("DELETE FROM conversation")
    suspend fun deleteAll()

    @Update
    suspend fun update(user: Conversation)

    @Update
    suspend fun update(user: List<Conversation>)

    @Update
    suspend fun updateAll(users: List<Conversation>)
}