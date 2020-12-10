package com.css.im_kit.db.repository

import android.content.Context
import com.css.im_kit.db.bean.User_Info
import com.css.im_kit.db.dao.UserInfoDao
import com.css.im_kit.db.imDb
import kotlinx.coroutines.flow.Flow
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

        fun getAll(): Flow<List<User_Info>> {
            return dao?.getAll() ?: flow { arrayListOf<User_Info>() }
        }

        suspend fun loadById(userId: String): User_Info? {
            return dao?.loadAllById(userId)
        }

        suspend fun insert(user: User_Info) {
            dao?.insert(user)
        }

        suspend fun insertDatas(users: List<User_Info>) {
            dao?.insertDatas(users)
        }

        suspend fun update(user: User_Info) {
            dao?.update(user)
        }

        suspend fun delete(user: User_Info) {
            dao?.delete(user)
        }

        suspend fun deleteAll() {
            dao?.deleteAll()
        }
    }
}