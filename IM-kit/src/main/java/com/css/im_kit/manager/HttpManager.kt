package com.css.im_kit.manager

import com.css.im_kit.IMManager
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.uiScope
import com.css.im_kit.http.Retrofit
import com.css.im_kit.model.conversation.Shop
import com.css.im_kit.model.userinfo.SGUserInfo
import com.css.im_kit.utils.generateSignature
import com.css.im_kit.utils.md5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

object HttpManager {
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
}

interface HttpCallBack {
    fun success(shop: Shop, sgUserInfo: SGUserInfo)
    fun fail()
}