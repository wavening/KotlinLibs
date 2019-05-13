package com.yww.utils.stragedy.brand.phones

import android.content.ComponentName
import android.content.Intent
import com.yww.utils.extension.packageName
import com.yww.utils.impl.IBrand

/**
 * @Author  WAVENING
 * @Date    2019/4/25-16:42
 */
internal class Huawei:IBrand.IManager {
    private val packagename = "packageName"
    private val huaweiManager = "com.huawei.systemmanager"
    private val huaweiManagerMain = "com.huawei.permissionmanager.ui.MainActivity"


    override val managerIntent: Intent by lazy {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(packagename, packageName)
        intent.component = ComponentName(huaweiManager, huaweiManagerMain)
        intent
    }

}