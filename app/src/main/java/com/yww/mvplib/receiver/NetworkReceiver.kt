package com.yww.mvplib.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast
import com.yww.utils.util.NetUtil

/**
 * @Author  WAVENING
 * @Date    2019/3/22-15:00
 */
open class NetworkReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ConnectivityManager.CONNECTIVITY_ACTION -> {
                Toast.makeText(context, "connect to internet!", Toast.LENGTH_SHORT).show()
                NetUtil.getNetworkType
            }
            else -> return
        }

    }
}