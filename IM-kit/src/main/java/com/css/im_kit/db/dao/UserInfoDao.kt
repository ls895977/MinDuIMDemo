package com.css.im_kit.db.dao

import androidx.room.*
import com.css.im_kit.db.bean.UserInfo

@Dao
interface UserInfoDao {
    @Query("SELECT * FROM userinfo")
    suspend fun getAll(): List<UserInfo>

    @Query("SELECT * FROM userinfo WHERE account = (:account)")
    suspend fun loadAllById(account: String): UserInfo

    @Insert
    suspend fun insert(users: UserInfo)

    @Insert
    suspend fun insertDatas(users: List<UserInfo>)

    @Delete
    suspend fun delete(user: UserInfo)

    @Query("DELETE FROM userinfo")
    suspend fun deleteAll()

    @Update
    suspend fun update(users: UserInfo)

    @Update
    suspend fun update(users: List<UserInfo>)

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