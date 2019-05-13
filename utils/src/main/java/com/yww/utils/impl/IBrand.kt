package com.yww.utils.impl

import android.content.Intent
import android.support.annotation.Keep

/**
 * @Author  WAVENING
 * @Date    2019/4/25-16:14
 * 手机品牌的抽象类
 */
@Keep
interface IBrand {

    fun openPhoneManager()


    interface IManager {
        val managerIntent: Intent
    }

    companion object {
        val COMMON    =  0x00000000
        val EMUI      =  0x00000001
        val FLYME     =  0x00000002
        val MIUI      =  0x00000003
        val SONY      =  0x00000004
        val OPPQ      =  0x00000005
        val VIVO      =  0x00000006
        val LG        =  0x00000007
        val LETV      =  0x00000008
        val QIHU      =  0x00000010
    }
}