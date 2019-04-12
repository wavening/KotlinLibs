package com.yww.mvplib.extension

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

/**
 * @author  WAVENING
 */
fun Context.toast(message:CharSequence, duration: Int = Toast.LENGTH_SHORT){
    try {
        if (isOnMainThread()) {
            Toast.makeText(applicationContext, message, duration).show()
        } else {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(applicationContext, message, duration).show()
            }
        }
    } catch (e: Exception) {
    }
}