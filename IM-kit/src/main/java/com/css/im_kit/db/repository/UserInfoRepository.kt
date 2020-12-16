package com.css.im_kit.db.repository

import android.content.Context
import com.css.im_kit.db.bean.UserInfo
import com.css.im_kit.db.dao.UserInfoDao
import com.css.im_kit.db.imDb

object UserInfoRepository {
    private var dao: UserInfoDao? = null

    fun build(context: Context) {
        dao = context.imDb().userDao
    }

    suspend fun getAll(): List<UserInfo>? {
        return dao?.getAll()
    }

    suspend fun loadById(userId: String): UserInfo? {
        return dao?.loadAllById(userId)
    }

    suspend fun insert(user: UserInfo) {
        dao?.insert(user)
    }

    suspend fun insertDatas(users: List<UserInfo>) {
        dao?.insertDatas(users)
    }

    suspend fun update(user: UserInfo) {
        dao?.update(user)
    }

    suspend fun deleteAll() {
        dao?.deleteAll()
    }

    /**
     * 修改昵称
     */
    suspend fun changeName(userId: String, username: String) {
        dao?.updateUserName(userId, username)
    }

    /**
     * 修改头像
     */
    suspend fun changeAvatar(userId: String, avatar: String) {
        dao?.updateAvatar(userId, avatar)
    }

    /**
     * 添加或者修改用户资料
     */
    suspend fun insertOrUpdateUser(user: UserInfo): Boolean {
        try {
            dao?.getAll()?.let { userInfos ->
                var isUpdate = false
                userInfos.forEach {
                    if (it.account == user.account) {
                        user.id = it.id
                        dao?.update(user)
                        isUpdate = true
                        return@forEach
                    }
                }
                if (!isUpdate) {
                    dao?.insert(user)
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}