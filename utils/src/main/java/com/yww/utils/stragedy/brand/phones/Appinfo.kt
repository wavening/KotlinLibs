package com.yww.utils.stragedy.brand.phones

import android.content.Intent
import com.yww.utils.extension.settingIntent
import com.yww.utils.impl.IBrand

/**
 * @Author  WAVENING
 * @Date    2019/4/25-17:55
 */
class Appinfo : IBrand.IManager {
    override val managerIntent: Intent = settingIntent()
}