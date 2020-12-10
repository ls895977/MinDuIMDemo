package com.css.im_kit.db.repository

import android.content.Context
import com.css.im_kit.db.bean.User_Info
import com.css.im_kit.db.dao.UserInfoDao
import com.css.im_kit.db.imDb
import kotlinx.coroutines.flow.Flow

data class UserInfoRepository(private val dao: UserInfoDao) {

    fun getAll(): Flow<List<User_Info>> {
        return dao.getAll()
    }

    suspend fun loadAllById(userId: String): User_Info ?{
        return dao.loadAllById(userId)
    }

    suspend fun insert(user: User_Info) {
        return dao.insert(user)
    }

    suspend fun insertDatas(users: List<User_Info>) {
        return dao.insertDatas(users)
    }

    suspend fun update(user: User_Info) {
        return dao.update(user)
    }

    suspend fun delete(user: User_Info) {
        return dao.delete(user)
    }

    suspend fun deleteAll() {
        return dao.deleteAll()
    }

    // 通过伴生对象实现单例模式
    companion object {
        @Volatile
        private var INSTANCE: UserInfoRepository? = null
        fun getInstance(context: Context): UserInfoRepository {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = UserInfoRepository(context.imDb().userDao)
                }
                return instance
            }
        }
    }
}