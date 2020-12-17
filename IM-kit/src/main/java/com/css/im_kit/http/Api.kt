package com.css.im_kit.http

import com.css.im_kit.model.conversation.HTTPConversation
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @FormUrlEncoded
    @POST("/chat/{url}")
    fun chatList(@Path("url") url: String
                 , @Field("app_id") app_id: String
                 , @Field("app_secret") app_secret: String
                 , @Field("account") account: String): Call<BaseData<MutableList<HTTPConversation>>>

}