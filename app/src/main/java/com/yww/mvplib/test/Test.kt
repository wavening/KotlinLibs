package com.yww.mvplib.test

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.yww.avoid.ActivityCallBack
import com.yww.avoid.AvoidResultActivityV7
import com.yww.http.manager.RetrofitUrlManager
import com.yww.mvplib.ui.activity.TestSingleIntentActivity
import com.yww.mvplib.utils.LogUtil
import com.yww.retrofit2base.request.HttpApi
import com.yww.utils.TestUtils
import com.yww.utils.manager.SpManager
import org.jetbrains.anko.toast

/**
 * @author  WAVENING
 */
class Test {
    fun log() {
        Log.e("http", "retrofit==" + RetrofitUrlManager.instance.dependence)
        Log.e("http", "retrofit==" + RetrofitUrlManager.instance)
        Log.e("http", "retrofit==" + RetrofitUrlManager.instance)
        HttpObserver<String>()

        Log.e("http", "http api==" + HttpApi.okHttpClient)
        Log.e("http", "http api==" + HttpApi.okHttpClient)
        Log.e("http", "http api==" + HttpApi.okHttpClientVar)
        Log.e("http", "http api==" + HttpApi.okHttpClientVar)
        Log.e("http", "http api==" + HttpApi.retrofit)
        Log.e("http", "http api==" + HttpApi.retrofit)
        Log.e("http", "http api==" + HttpApi.retrofitVar)
        Log.e("http", "http api==" + HttpApi.retrofitVar)

        Log.e("http", "request api==" + RequestApi.instance)
        Log.e("http", "request api==" + RequestApi.instance)
        Log.e("http", "request api==" + RequestApi.instance.okHttpClient1())
        Log.e("http", "request api==" + RequestApi.instance.okHttpClient1())
        Log.e("http", "request api==" + RequestApi.instance.okHttpClient2())
        Log.e("http", "request api==" + RequestApi.instance.okHttpClient2())
        Log.e("http", "request api==" + RequestApi.instance.retrofit1())
        Log.e("http", "request api==" + RequestApi.instance.retrofit1())
        Log.e("http", "request api==" + RequestApi.instance.retrofit2())
        Log.e("http", "request api==" + RequestApi.instance.retrofit2())
    }

    fun startNextActivity(activity: AppCompatActivity) {
        AvoidResultActivityV7(activity).startForResult(TestSingleIntentActivity::class.java, 4,
            object : ActivityCallBack {
                override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                    activity.toast(
                        "request_code==" + requestCode
                                + "\nresult_code==" + resultCode
                                + "\ndata==" + data
                    )
                }
            })
    }

    fun testSpManager() {
        SpManager.initManager("first")
        LogUtil.log("name==" + SpManager.name + "\n instance1==" + SpManager.instance)
        LogUtil.log("name==" + SpManager.name + "\n instance2==" + SpManager.instance)
        SpManager.initManager("second")
        LogUtil.log("name==" + SpManager.name + "\n instance3==" + SpManager.instance)
        LogUtil.log("name==" + SpManager.name + "\n instance4==" + SpManager.instance)

        LogUtil.log("map==" + SpManager.instance?.getSize())
    }

    fun testPermissionManager(activity: AppCompatActivity) {
        TestUtils.test1(activity)
    }

    fun testDownload() {
        TestUtils.testDownloadSetting()
    }
}