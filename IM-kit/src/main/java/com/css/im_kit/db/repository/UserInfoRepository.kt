package com.css.im_kit.db.repository

import android.content.Context
import com.css.im_kit.db.bean.UserInfo
import com.css.im_kit.db.dao.UserInfoDao
import com.css.im_kit.db.imDb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class UserInfoRepository {

    // 通过伴生对象实现单例模式
    companion object {
        private var dao: UserInfoDao? = null

        @Volatile
        private var INSTANCE: UserInfoRepository? = null
        fun build(context: Context): UserInfoRepository {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    dao = context.imDb().userDao
                    instance = UserInfoRepository()
                }
                return instance
            }
        }

        fun getAll(): Flow<List<UserInfo>> {
            return dao?.getAll() ?: flow { arrayListOf<UserInfo>() }
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
                dao?.getAll()?.collect { userInfos ->
                    var isUpdate = false
                    userInfos.forEach {
                        if (it.userId == user.userId) {
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
}