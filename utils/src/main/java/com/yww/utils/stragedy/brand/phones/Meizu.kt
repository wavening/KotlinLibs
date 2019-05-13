package com.yww.utils.stragedy.brand.phones

import android.content.Intent
import com.yww.utils.extension.packageName
import com.yww.utils.impl.IBrand

/**
 * @Author  WAVENING
 * @Date    2019/4/25-17:45
 */
internal class Flyme : IBrand.IManager {

    private val packagename = "packageName"
    private val flymeAction = "com.meizu.safe.security.SHOW_APPSEC"


    override val managerIntent: Intent
            by lazy {
                val intent = Intent(flymeAction)
                intent.categories.add(Intent.CATEGORY_DEFAULT)
                intent.putExtra(packagename, packageName)
                intent
            }
}