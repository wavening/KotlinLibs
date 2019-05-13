package com.yww.utils.stragedy.brand.phones

import android.content.Intent
import com.yww.utils.extension.packageManager
import com.yww.utils.impl.IBrand

/**
 * @Author  WAVENING
 * @Date    2019/4/29-13:46
 */
internal class Coolpad : IBrand.IManager {
    private val manager = "com.yulong.android.security:remote"
    override val managerIntent: Intent by lazy {
        packageManager?.getLaunchIntentForPackage(manager)!!
    }
}