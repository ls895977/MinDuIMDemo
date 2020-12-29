package com.css.im_kit.http

import com.css.im_kit.http.bean.AssignCustomerBack
import com.css.im_kit.http.bean.MessageHistoryBack
import com.css.im_kit.manager.QNTokenBean
import com.css.im_kit.model.conversation.HTTPConversation
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

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

    @POST
    fun getQiuNiuTokenUrl(@Url url: String, @Body requestBody: RequestBody): Call<BaseData<QNTokenBean>>

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

    /**
     * 用户修改信息
     */
    @FormUrlEncoded
    @POST("/user/modify")
    fun userModify(@Field("app_id") app_id: String,
                   @Field("account") account: String,
                   @Field("nickname") nickname: String,
                   @Field("avatar") avatar: String,
                   @Field("sign") sign: String,
                   @Field("nonce_str") nonce_str: String): Call<BaseData<Any>>

    /**
     * 历史记录
     */
    @POST("/chat/messageHistory")
    fun messageHistory(@Body requestBody: RequestBody): Call<BaseData<MessageHistoryBack>>

    /**
     * 把消息置为已读
     */
    @POST("/chat/changRead")
    fun changRead(@Body requestBody: RequestBody): Call<BaseData<Any>>

}