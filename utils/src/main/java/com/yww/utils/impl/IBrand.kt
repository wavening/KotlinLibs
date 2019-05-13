package com.yww.utils.impl

import android.content.Intent
import android.support.annotation.Keep

/**
 * @Author  WAVENING
 * @Date    2019/4/25-16:14
 * 手机品牌的抽象类
 */
@Keep
interface IPhoneBrand {
    fun verifyPhoneBrand()

    interface IPhoneManager {
        val  managerIntent:Intent
        fun openPhoneManager(): Intent
    }
}