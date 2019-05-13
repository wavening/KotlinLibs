package com.yww.utils.stragedy.brand.phones

import android.content.ComponentName
import android.content.Intent
import com.yww.utils.extension.packageName
import com.yww.utils.impl.IBrand

/**
 * @Author  WAVENING
 * @Date    2019/4/29-11:01
 */
internal class Letv : IBrand.IManager {
    private val manager = "com.letv.android.letvsafe"
    private val managerMain = "com.letv.android.letvsafe.PermissionAndApps"

    override val managerIntent: Intent by lazy {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", packageName)
        intent.component = ComponentName(manager, managerMain)
        intent
    }

}