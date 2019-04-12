package com.yww.retrofit2base.request

import com.yww.http.manager.RetrofitUrlManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author  WAVENING
 */
open class HttpApi {

    private object Holder {
        val INSTANCE = HttpApi()
    }

    companion object {
        val instance = Holder.INSTANCE

        val okHttpClient: OkHttpClient = RetrofitUrlManager.instance.with(OkHttpClient.Builder())
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(10000, TimeUnit.MILLISECONDS)
            .writeTimeout(10000,TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .build()

        val okHttpClientVar: OkHttpClient
            get() = RetrofitUrlManager.instance.with(OkHttpClient.Builder())
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .build()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Api.baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            //使用Gson
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val retrofitVar: Retrofit
            get() = Retrofit.Builder()
                .baseUrl(Api.baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //使用Gson
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
    }


}