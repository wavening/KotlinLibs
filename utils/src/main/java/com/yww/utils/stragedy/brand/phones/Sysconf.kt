package com.yww.utils.stragedy.brand.phones

import android.content.Intent
import com.yww.utils.impl.IBrand

/**
 * @Author  WAVENING
 * @Date    2019/4/29-11:20
 */
internal class Sysconf : IBrand.IManager {
    private val setting = "android.settings.SETTINGS"
    override val managerIntent: Intent by lazy {
        Intent(setting)
    }

}