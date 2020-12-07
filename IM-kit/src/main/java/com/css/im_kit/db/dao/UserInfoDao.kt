package com.css.im_kit.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.css.im_kit.db.bean.UserInfo

@Dao
interface UserInfoDao {
    @Query("SELECT * FROM user_info")
    suspend fun getAll(): LiveData<Array<UserInfo>>

    @Query("SELECT * FROM user_info WHERE userId IN (:userIds)")
    suspend fun loadAllByIds(userIds: IntArray): LiveData<Array<UserInfo>>

    @Insert
    suspend fun insert(vararg users: UserInfo):Long

    @Delete
    suspend fun delete(vararg user: UserInfo):Int

    @Query("DELETE FROM user_info")
    suspend fun deleteAll():Int

    @Update
    suspend fun update(vararg users: UserInfo):Int
}