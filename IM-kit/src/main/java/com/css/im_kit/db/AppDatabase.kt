package com.css.im_kit.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.bean.UserInfo
import com.css.im_kit.db.dao.MessageDao
import com.css.im_kit.db.dao.UserInfoDao

@Database(entities = [UserInfo::class, Message::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val userDao: UserInfoDao
    abstract val messageDao: MessageDao


    // 通过伴生对象实现单例模式
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context,
                            AppDatabase::class.java, "css_im"
                    ).build()
                }
                return instance
            }
        }
    }
}