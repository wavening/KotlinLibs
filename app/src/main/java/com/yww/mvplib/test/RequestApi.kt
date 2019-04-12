package com.yww.mvplib.test

import com.yww.retrofit2base.request.HttpApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * @author  WAVENING
 */
class RequestApi private constructor() : HttpApi() {
    private object Holder {
        val INSTANCE = RequestApi()
    }

    companion object {
        val instance = Holder.INSTANCE

    }

    fun okHttpClient1(): OkHttpClient = okHttpClient
    fun okHttpClient2(): OkHttpClient = okHttpClientVar
    fun retrofit1(): Retrofit = retrofit
    fun retrofit2(): Retrofit = retrofitVar

}