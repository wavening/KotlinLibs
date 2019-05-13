package com.yww.utils.manager

import android.support.annotation.Keep

/**
 * @Author  WAVENING
 * @Date    2019/4/25-16:22
 */
@Keep
class PhoneBrandManager {
    private object Holder {
        val INSTANCE = PhoneBrandManager()
    }

    companion object {
        val instance: PhoneBrandManager = Holder.INSTANCE
    }
}