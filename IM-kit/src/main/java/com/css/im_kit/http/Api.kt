package com.css.im_kit.http

import com.css.im_kit.model.conversation.HTTPConversation
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface Api {
    @FormUrlEncoded
    @POST("/chat/{url}")
    fun chatList(@Path("url") url: String,
                 @Field("app_id") app_id: String,
                 @Field("account") account: String,
                 @Field("sign") sign: String,
                 @Field("nonce_str") nonce_str: String
    ): Call<BaseData<MutableList<HTTPConversation>>>

}