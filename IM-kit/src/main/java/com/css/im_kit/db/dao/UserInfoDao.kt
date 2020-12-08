package com.css.im_kit.db.dao

import androidx.room.*
import com.css.im_kit.db.bean.User_Info
import kotlinx.coroutines.flow.Flow

@Dao
interface UserInfoDao {
    @Query("SELECT * FROM user_info")
    fun getAll(): Flow<List<User_Info>>

    @Query("SELECT * FROM user_info WHERE userId IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): Flow<List<User_Info>>

    @Insert
    suspend fun insert(users: User_Info)

    @Delete
    suspend fun delete(user: User_Info)

    @Query("DELETE FROM user_info")
    suspend fun deleteAll()

    @Update
    suspend fun update(users: User_Info)
}