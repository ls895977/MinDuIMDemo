package com.css.im_kit.db.repository

import androidx.lifecycle.LiveData
import com.css.im_kit.db.bean.UserInfo
import com.css.im_kit.db.dao.UserInfoDao

class UserInfoRepository(private val dao: UserInfoDao) {

    suspend fun getAll(): LiveData<Array<UserInfo>> {
        return dao.getAll()
    }

    suspend fun insert(user: UserInfo): Long {
        return dao.insert(user)
    }

    suspend fun update(user: UserInfo): Int {
        return dao.update(user)
    }

    suspend fun delete(user: UserInfo): Int {
        return dao.delete(user)
    }

    suspend fun deleteAll(): Int {
        return dao.deleteAll()
    }
}