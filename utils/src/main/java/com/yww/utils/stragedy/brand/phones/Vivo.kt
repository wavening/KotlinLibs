package com.yww.utils.stragedy.brand.phones

import android.content.Intent
import com.yww.utils.extension.packageManager
import com.yww.utils.impl.IBrand

/**
 * @Author  WAVENING
 * @Date    2019/4/29-13:53
 */
internal class Vivo :IBrand.IManager{
    private val manager = "com.vivo.securedaemonservice"
    override val managerIntent: Intent by lazy {
        packageManager?.getLaunchIntentForPackage(manager)!!
    }
}