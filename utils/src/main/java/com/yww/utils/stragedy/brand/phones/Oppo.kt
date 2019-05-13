package com.yww.utils.stragedy.brand.phones

import android.content.ComponentName
import android.content.Intent
import com.yww.utils.extension.packageName
import com.yww.utils.impl.IBrand

/**
 * @Author  WAVENING
 * @Date    2019/4/25-16:42
 */
internal class Oppo:IBrand.IManager {
    private val manager = "com.color.safecenter"
    private val managerMain = "com.color.safecenter.permission.PermissionManagerActivity"

    override val managerIntent: Intent by lazy {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", packageName)
        intent.component = ComponentName(manager, managerMain)
        intent
    }

}