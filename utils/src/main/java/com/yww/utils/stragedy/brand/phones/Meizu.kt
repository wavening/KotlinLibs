package com.yww.utils.stragedy.brand.phones

import android.content.Intent
import com.yww.utils.extension.packageName
import com.yww.utils.impl.IBrand

/**
 * @Author  WAVENING
 * @Date    2019/4/25-17:45
 */
internal class Meizu : IBrand.IManager {

    private val meizuAction = "com.meizu.safe.security.SHOW_APPSEC"


    override val managerIntent: Intent
            by lazy {
                val intent = Intent(meizuAction)
                intent.categories.add(Intent.CATEGORY_DEFAULT)
                intent.putExtra("packageName", packageName)
                intent
            }
}