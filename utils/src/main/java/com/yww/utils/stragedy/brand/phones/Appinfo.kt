package com.yww.utils.stragedy.brand.phones

import android.content.Intent
import android.net.Uri
import android.os.Build
import com.yww.utils.extension.packageName
import com.yww.utils.impl.IBrand

/**
 * @Author  WAVENING
 * @Date    2019/4/25-17:55
 */
class BrandC : IBrand.IManager {
    private val settingAction = "android.settings.APPLICATION_DETAILS_SETTINGS"
    private val viewAction = "android.intent.action.VIEW"
    private val packageS = "package"
    private  val packagename = "com.android.settings.ApplicationPkgName"
    private  val manager = "com.android.settings"
    private  val managerMain = "com.android.settings.InstalledAppDetails"

    override val managerIntent: Intent by lazy {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            true -> {
                intent.action = settingAction
                intent.data = Uri.fromParts(packageS, packageName,null)
            }
            false
            -> {
                intent.action = viewAction
                intent.setClassName(manager,managerMain)
                intent.putExtra(packagename, packageName)
            }
        }
        intent
    }
}