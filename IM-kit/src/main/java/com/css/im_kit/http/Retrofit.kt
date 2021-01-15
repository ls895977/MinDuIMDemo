package com.css.im_kit.http

import android.util.Log
import com.css.im_kit.BuildConfig
import com.css.im_kit.IMManager
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Retrofit {
    var retrofit: Retrofit? = null
    var api: Api? = null
    fun initRetrofit() {
        retrofit = Retrofit.Builder()
                .baseUrl(IMManager.baseUrl)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        api = retrofit?.create(Api::class.java)

    }

    private fun getHeaderInterceptor(): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
            //添加Token
            requestBuilder.header(if (IMManager.isBusiness) "myToken" else "token", IMManager.getAPPToken())
            val request = requestBuilder.build()
            return@Interceptor chain.proceed(request)
        }
    }

    var httpClient: OkHttpClient? = null

    private fun getOkHttpClient(): OkHttpClient {
        if (httpClient == null) {
            return if (BuildConfig.DEBUG) {
                OkHttpClient().newBuilder()
                        .readTimeout(60000, TimeUnit.MILLISECONDS)
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .writeTimeout(60000, TimeUnit.MILLISECONDS)
                        .connectionPool(ConnectionPool(32, 5, TimeUnit.MINUTES))
                        .retryOnConnectionFailure(true)
                        //设置Header
                        .addInterceptor(getHeaderInterceptor())
                        .addInterceptor(getInterceptor())
                        .build()
            } else {
                OkHttpClient().newBuilder()
                        .readTimeout(60000, TimeUnit.MILLISECONDS)
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .writeTimeout(60000, TimeUnit.MILLISECONDS)
                        .connectionPool(ConnectionPool(32, 5, TimeUnit.MINUTES))
                        .retryOnConnectionFailure(true)
                        .addInterceptor(getHeaderInterceptor())
                        .addInterceptor(getInterceptor())
                        //设置Header
                        .build()
            }.also { httpClient = it }
        } else {
            return httpClient!!
        }

    }

    /**
     * 设置拦截器 打印日志
     *
     * @return
     */
    private fun getInterceptor(): Interceptor {
        val interceptor = HttpLoggingInterceptor { message -> Log.e("HttpLoggingInterceptor", message) }
        //显示日志
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }
}

