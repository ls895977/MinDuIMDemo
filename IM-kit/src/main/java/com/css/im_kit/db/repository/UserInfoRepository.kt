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

    @Synchronized
    suspend fun getAll(): List<UserInfo>? {
        return dao?.getAll()
    }

    @Synchronized
    suspend fun loadById(userId: String): UserInfo? {
        return dao?.loadAllById(userId)
    }

    @Synchronized
    suspend fun insert(user: UserInfo) {
        dao?.insert(user)
    }

    @Synchronized
    suspend fun insertDatas(users: List<UserInfo>) {
        val dbUsers = arrayListOf<UserInfo>()
        val noDbUsers = arrayListOf<UserInfo>()
        val myDbUser = dao?.getAll()
        users.filter { t1 ->
            myDbUser?.forEach { t2 ->
                if (t1.account == t2.account) {
                    dbUsers.add(t2)
                    return@filter false
                }
            }
            return@filter true
        }.map {
            it.id = 0
            return@map it
        }.let {
            noDbUsers.addAll(it)
        }
        dao?.update(dbUsers)
        dao?.insertDatas(noDbUsers)
    }

    @Synchronized
    suspend fun update(user: UserInfo) {
        dao?.update(user)
    }

    @Synchronized
    suspend fun deleteAll() {
        dao?.deleteAll()
    }

    /**
     * 修改昵称
     */
    @Synchronized
    suspend fun changeName(userId: String, username: String) {
        dao?.updateUserName(userId, username)
    }

    /**
     * 修改头像
     */
    @Synchronized
    suspend fun changeAvatar(userId: String, avatar: String) {
        dao?.updateAvatar(userId, avatar)
    }

    /**
     * 添加或者修改用户资料
     */
    @Synchronized
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