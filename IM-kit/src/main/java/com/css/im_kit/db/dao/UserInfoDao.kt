package com.css.im_kit.db.dao

import androidx.room.*
import com.css.im_kit.db.bean.UserInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface UserInfoDao {
    @Query("SELECT * FROM userinfo")
    fun getAll(): Flow<List<UserInfo>>

    @Query("SELECT * FROM userinfo WHERE account = (:account)")
    suspend fun loadAllById(account: String): UserInfo

    @Insert
    suspend fun insert(users: UserInfo)

    @Insert
    suspend fun insertDatas(users: List<UserInfo>)

    @Delete
    suspend fun delete(user: UserInfo)

    @Query("DELETE FROM userinfo")
    fun deleteAll()

    @Update
    suspend fun update(users: UserInfo)

    /**
     * 修改昵称
     */
    @Query("UPDATE userinfo SET nickName = :userName WHERE account = :userId")
    suspend fun updateUserName(userId: String, userName: String)

    /**
     * 修改头像
     */
    @Query("UPDATE userinfo SET avatar = :avatar WHERE account = :userId")
    suspend fun updateAvatar(userId: String, avatar: String)
}