package com.css.im_kit.http

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit {
    var retrofit: Retrofit? = null
    var api: Api? = null
    private fun initRetrofit() {
        val okHttpClient = OkHttpClient.Builder()
        retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        api = retrofit?.create(Api::class.java)
    }
}