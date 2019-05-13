package com.yww.utils.util

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build

/**
 * @author  WAVENING
 */
object NetUtil {

    val getNetworkType: Unit = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> getTypeAtOver23()
        else -> getTypeBelow23()
    }


    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.M)
    private fun getTypeAtOver23(): Unit {
        val service: ConnectivityManager =
            Util.getApplication()?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            service.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network?) {
                    val networkInfo = service.getNetworkInfo(network)
                    LogUtil.log("23 default network available == " + networkInfo.subtypeName ?: "网络信息为空")
                }
            })
        service.addDefaultNetworkActiveListener {
            //onNetworkActive
            LogUtil.log("23 default network activate")
        }
        val allNetworks = service.allNetworks
        for (network in allNetworks) {
            val networkInfo = service.getNetworkInfo(network)
            if (networkInfo.isConnected)
                LogUtil.log("23 network connected == " + networkInfo.subtypeName)
        }

    }


    @SuppressLint("MissingPermission")
    private fun getTypeBelow23(): Unit {
        val service: ConnectivityManager =
            Util.getApplication()?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mobileNetInfo = service.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (mobileNetInfo.isConnected)
            LogUtil.log("mobile net info == " + mobileNetInfo.subtypeName)
        val wifiNetInfo = service.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (wifiNetInfo.isConnected)
            LogUtil.log("mobile net info == " +"\n subtype="+ wifiNetInfo.subtype +"\nsubname="+wifiNetInfo.subtypeName)
    }
}