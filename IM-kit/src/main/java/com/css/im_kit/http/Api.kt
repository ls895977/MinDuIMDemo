package com.css.im_kit.http

import com.css.im_kit.http.bean.AssignCustomerBack
import com.css.im_kit.manager.QNTokenBean
import com.css.im_kit.model.conversation.HTTPConversation
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url

interface Api {
    /**
     * 聊天列表
     */
    @FormUrlEncoded
    @POST
    fun chatList(@Url url: String,
                 @Field("app_id") app_id: String,
                 @Field("account") account: String,
                 @Field("sign") sign: String,
                 @Field("nonce_str") nonce_str: String
    ): Call<BaseData<MutableList<HTTPConversation>>>

    @FormUrlEncoded
    @POST
    fun getQiuNiuTokenUrl(@Url url: String, @Field("source") source: String): Call<BaseData<QNTokenBean>>

    /**
     * 分配客服
     */
    @FormUrlEncoded
    @POST("/chat/assignCustomer")
    fun assignCustomer(@Field("app_id") app_id: String,
                       @Field("account") account: String,
                       @Field("shop_id") shop_id: String,
                       @Field("sign") sign: String,
                       @Field("nonce_str") nonce_str: String): Call<BaseData<AssignCustomerBack>>

}