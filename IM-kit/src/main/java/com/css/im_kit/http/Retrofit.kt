package com.css.im_kit.http

import com.css.im_kit.IMManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit {
    var retrofit: Retrofit? = null
    var api: Api? = null
    fun initRetrofit() {
        val okHttpClient = OkHttpClient.Builder()
        retrofit = Retrofit.Builder()
                .baseUrl(IMManager.baseUrl)
                .client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        api = retrofit?.create(Api::class.java)
    }
}