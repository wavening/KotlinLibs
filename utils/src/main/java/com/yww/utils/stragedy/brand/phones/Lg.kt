package com.yww.utils.stragedy.brand.phones

import android.content.ComponentName
import android.content.Intent
import com.yww.utils.extension.packageName
import com.yww.utils.impl.IBrand

/**
 * @Author  WAVENING
 * @Date    2019/4/29-10:59
 */
internal class Lg :IBrand.IManager{
    private val manager = "com.android.settings"
    private val managerMain = "com.android.settings.Settings\$AccessLockSummaryActivity"

    override val managerIntent: Intent by lazy {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", packageName)
        intent.component = ComponentName(manager, managerMain)
        intent
    }
}