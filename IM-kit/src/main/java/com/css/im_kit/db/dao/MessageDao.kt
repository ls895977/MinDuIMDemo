package com.css.im_kit.db.dao

import androidx.room.*
import com.css.im_kit.db.bean.Message

@Dao
interface MessageDao {

    @Query("SELECT  * FROM message WHERE shop_id = (:shop_id) ORDER BY receive_time ASC")
    suspend fun getMessages(shop_id: String): List<Message>

    @Query("SELECT  * FROM message WHERE m_id = (:messageId) ORDER BY receive_time ASC")
    suspend fun getMessage4MessageId(messageId: String): List<Message>

    @Query("SELECT  * FROM message WHERE shop_id = (:shop_id) AND receive_time <:lastItemTime ORDER BY receive_time DESC LIMIT :pageSize")
    suspend fun getMessages(shop_id: String, lastItemTime: Long, pageSize: Int): MutableList<Message>

    @Query("SELECT  * FROM message WHERE (send_account = (:chat_account) OR receive_account = (:chat_account)) AND receive_time <:lastItemTime ORDER BY receive_time DESC LIMIT :pageSize")
    suspend fun getMessages4Account(chat_account: String, lastItemTime: Long, pageSize: Int): MutableList<Message>

    @Query("SELECT  * FROM message WHERE shop_id = (:shop_id)  ORDER BY receive_time DESC LIMIT 1")
    suspend fun getLast(shop_id: String): Message

    @Query("SELECT  * FROM message ORDER BY receive_time DESC LIMIT 1")
    suspend fun getLast(): Message

    @Query("SELECT  * FROM message  WHERE shop_id = (:shop_id) ORDER BY receive_time ASC LIMIT 1")
    suspend fun getFirst(shop_id: String): Message

    @Query("SELECT id FROM message WHERE shop_id = (:shop_id) AND read_status = :read_status AND send_account !=:send_account")
    suspend fun getNoReadData(shop_id: String, read_status: Int, send_account: String): List<Int>

    @Query("UPDATE message SET send_status = :sendType WHERE m_id IN (:messageIds) ")
    suspend fun changeMessageSendType(sendType: Int, messageIds: List<String>)

    @Query("SELECT  * FROM message WHERE m_id = (:messageIds)  ORDER BY receive_time DESC LIMIT 1")
    suspend fun getMessage4messageId(messageIds: String): Message

    @Query("UPDATE message SET message = :message WHERE m_id  = :messageId ")
    suspend fun changeMessageContent(message: String, messageId: String)

    @Insert
    suspend fun insert(users: Message)

    @Insert
    suspend fun insertDatas(users: List<Message>)

    @Delete
    suspend fun delete(user: Message)

    @Query("DELETE FROM message")
    suspend fun deleteAll()

    @Query("DELETE FROM message WHERE m_id  = :messageId ")
    suspend fun delete(messageId: String)

    @Update
    suspend fun update(users: Message)

    @Update
    suspend fun update(users: List<Message>)

    /**
     * 设置消息已读
     */
    @Query("UPDATE message SET read_status = 1 WHERE read_status = 0 AND (send_account = (:chat_account) OR receive_account = (:chat_account))")
    suspend fun read(chat_account: String)
}