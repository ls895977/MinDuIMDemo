package com.css.im_kit.http

import com.css.im_kit.model.conversation.SGConversation
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {
    @GET("{url}")
    fun chatList(@Path("url") url: String
                 , @Field("app_id") app_id: String
                 , @Field("app_secret") app_secret: String
                 , @Field("account") account: String): Call<List<SGConversation>>

}