package com.css.im_kit.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.css.im_kit.db.bean.Conversation
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.bean.User_Info
import com.css.im_kit.db.dao.ConversationDao
import com.css.im_kit.db.dao.MessageDao
import com.css.im_kit.db.dao.UserInfoDao

@Database(entities = [User_Info::class, Message::class, Conversation::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract val userDao: UserInfoDao
    abstract val messageDao: MessageDao
    abstract val conversationDao: ConversationDao


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