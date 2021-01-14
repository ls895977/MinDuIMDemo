package com.css.im_kit.manager

import com.css.im_kit.IMManager
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.bean.UserInfo
import com.css.im_kit.db.gson
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.MessageRepository
import com.css.im_kit.db.repository.UserInfoRepository
import com.css.im_kit.db.uiScope
import com.css.im_kit.http.Retrofit
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.model.conversation.Shop
import com.css.im_kit.model.userinfo.SGUserInfo
import com.css.im_kit.utils.generateSignature
import com.css.im_kit.utils.long10
import com.css.im_kit.utils.md5
import com.css.im_kit.utils.toMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.awaitResponse

object HttpManager {
    /**
     * 分配客服
     */
    @Synchronized
    fun assignCustomer(shopId: String, callBack: HttpCallBack) {
        uiScope.launch {
            withContext(Dispatchers.Default) {
                val nonceStr = System.currentTimeMillis().toString().md5()
                val map = HashMap<String, String>()
                map["account"] = IMManager.account ?: ""
                map["app_id"] = IMManager.app_id ?: ""
                map["shop_id"] = shopId
                map["nonce_str"] = nonceStr
                Retrofit.api?.assignCustomer(
                        nonce_str = nonceStr,
                        app_id = IMManager.app_id ?: "",
                        shop_id = shopId,
                        account = IMManager.account ?: "",
                        sign = map.generateSignature(IMManager.app_secret ?: "")
                )?.awaitResponse()?.let {
                    if (it.isSuccessful) {
                        it.body()?.apply {
                            if (code == "20000") {
                                return@let data
                            } else {
                                callBack.fail()
                                return@let null
                            }
                        }
                    } else {
                        callBack.fail()
                    }
                    return@let null
                }?.let {
                    val sgUserInfo = SGUserInfo(
                            avatar = it.avatar,
                            nickname = it.nickname,
                            account = it.account,
                            user_type = it.user_type
                    )
                    it.chat_shop?.let { shop ->
                        callBack.success(shop, sgUserInfo)
                    }
                }
            }
        }
    }

    /**
     * 用户修改信息
     */
    @Synchronized
    fun modifyUserInfo(sgUserInfo: SGUserInfo, callBack: HttpModifyUserInfoCallBack) {
        uiScope.launch {
            withContext(Dispatchers.Default) {
                val nonceStr = System.currentTimeMillis().toString().md5()
                val map = HashMap<String, String>()
                map["account"] = sgUserInfo.account ?: ""
                map["app_id"] = IMManager.app_id ?: ""
                map["nickname"] = sgUserInfo.nickname ?: ""
                map["avatar"] = sgUserInfo.avatar ?: ""
                map["nonce_str"] = nonceStr
                Retrofit.api?.userModify(
                        nonce_str = nonceStr,
                        app_id = IMManager.app_id ?: "",
                        nickname = sgUserInfo.nickname ?: "",
                        avatar = sgUserInfo.avatar ?: "",
                        account = sgUserInfo.account ?: "",
                        sign = map.generateSignature(IMManager.app_secret ?: "")
                )?.awaitResponse()?.let {
                    if (it.isSuccessful) {
                        if (it.body()?.code == "20000") {
                            return@let sgUserInfo
                        }
                    }
                    return@let null
                }.let {
                    if (it == null) {
                        callBack.fail()
                    } else {
                        callBack.success(it)
                    }
                }
            }
        }
    }

    /**
     * 历史消息
     */
    @Synchronized
    suspend fun messageHistory(shopId: String,
                               receive_account: String,
                               time: Long,
                               page: String,
                               flag: String,
                               pageSize: Int)
            : MutableList<Message>? = withContext(Dispatchers.Default) {
        val nonceStr = System.currentTimeMillis().toString().md5()
        val map = HashMap<String, String>()
        map["app_id"] = IMManager.app_id ?: ""
        map["account"] = IMManager.account ?: ""
        map["shop_id"] = shopId
        if (IMManager.isBusiness) {
            map["receive_account"] = receive_account
        }
        map["page"] = page
        map["size"] = pageSize.toString()
        map["time"] = time.long10().minus(1).toString()
        map["flag"] = flag
        map["nonce_str"] = nonceStr
        val body = HashMap<String, Any>()
        body["nonce_str"] = nonceStr
        body["app_id"] = IMManager.app_id ?: ""
        body["shop_id"] = shopId
        if (IMManager.isBusiness) {
            body["receive_account"] = receive_account
        }
        body["page"] = page
        body["size"] = pageSize.toString()
        body["time"] = time.long10().minus(1).toString()
        body["flag"] = flag
        body["account"] = IMManager.account ?: ""
        body["sign"] = map.generateSignature(IMManager.app_secret ?: "")
        return@withContext Retrofit.api?.messageHistory(
                requestBody = gson.toJson(body).toRequestBody("application/json".toMediaType())
        )?.awaitResponse()?.let {
            if (it.isSuccessful) {
                if (it.body()?.code == "20000") {
                    return@let it.body()?.data?.data
                }
            }
            return@let null
        }?.also {//查看历史记录中用户资料
            val userInfos = arrayListOf<UserInfo>()
            it.forEach { item ->
                if (!userInfos.contains(item.send_account_info)) {
                    item.send_account_info?.let { it1 -> userInfos.add(it1) }
                }
                if (!userInfos.contains(item.receive_account_info)) {
                    item.receive_account_info?.let { it1 -> userInfos.add(it1) }
                }
            }
            UserInfoRepository.insertDatas(userInfos)
        }?.let {
            val messages = arrayListOf<Message>()
            it.forEach {
                messages.add(it.toMessage())
            }
            return@let messages
        }?.let {
            if (it.isNullOrEmpty()) return@withContext arrayListOf<Message>()
            MessageRepository.insertDatas(it)
            if (IMManager.isBusiness) {
                return@withContext MessageRepository.getMessage4Account(chat_account = receive_account, pageSize = pageSize, lastItemTime = time)
            } else {
                return@withContext MessageRepository.getMessage(shop_id = shopId, pageSize = pageSize, lastItemTime = time)
            }
        }
    }


    /**
     * 把消息置为已读
     */
    @Synchronized
    suspend fun changRead(conversation: SGConversation, m_ids: MutableList<String>) {
        ioScope.launch {
            withContext(Dispatchers.Default) {

                val nonceStr = System.currentTimeMillis().toString().md5()
                val map = HashMap<String, String>()
                map["app_id"] = IMManager.app_id ?: ""
                map["nonce_str"] = nonceStr
                val body = HashMap<String, Any>()
                body["app_id"] = IMManager.app_id ?: ""
                body["m_ids"] = m_ids
                body["sign"] = map.generateSignature(IMManager.app_secret ?: "")
                body["nonce_str"] = nonceStr

                Retrofit.api?.changRead(
                        requestBody = gson.toJson(body).toRequestBody("application/json".toMediaType())
                )?.awaitResponse()?.let {
                    if (it.isSuccessful) {
                        if (it.body()?.code == "20000") {
                            MessageRepository.read(m_ids)
                            IMMessageManager.unreadMessageNumCount(
                                    conversation.shop_id ?: "",
                                    conversation.account ?: "",
                                    conversation.chat_account ?: "",
                                    m_ids.size,
                                    false)

                        }
                    }
                }
            }
        }
    }

    /**
     * 把消息置为已读
     */
    @Synchronized
    suspend fun changReadSomeOne(chat_account: String, shop_id: String): Boolean =
            withContext(Dispatchers.Default) {
                val nonceStr = System.currentTimeMillis().toString().md5()
                val map = HashMap<String, String>()
                map["app_id"] = IMManager.app_id ?: ""
                map["account"] = IMManager.account ?: ""
                if (!IMManager.isBusiness) {
                    map["shop_id"] = shop_id
                }
                map["chat_account"] = chat_account
                map["nonce_str"] = nonceStr
                val body = HashMap<String, Any>()
                body["app_id"] = IMManager.app_id ?: ""
                body["account"] = IMManager.account ?: ""
                if (!IMManager.isBusiness) {
                    body["shop_id"] = shop_id
                }
                body["chat_account"] = chat_account
                body["sign"] = map.generateSignature(IMManager.app_secret ?: "")
                body["nonce_str"] = nonceStr

                Retrofit.api?.changReadSomeOne(
                        requestBody = gson.toJson(body).toRequestBody("application/json".toMediaType())
                )?.awaitResponse()?.let {
                    if (it.isSuccessful) {
                        if (it.body()?.code == "20000") {
                            MessageRepository.read(chat_account)
                            return@withContext true
                        }
                    }
                }
                return@withContext false
            }

}


interface HttpCallBack {
    fun success(shop: Shop, sgUserInfo: SGUserInfo)
    fun fail()
}

interface HttpModifyUserInfoCallBack {
    fun success(sgUserInfo: SGUserInfo)
    fun fail()
}