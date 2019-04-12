package com.yww.mvplib.extension

import android.os.Looper

/**
 * @author  WAVENING
 */
fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()