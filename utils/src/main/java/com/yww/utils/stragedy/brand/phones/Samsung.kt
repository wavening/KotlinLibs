package com.yww.utils.stragedy.brand.phones

import android.content.Intent
import com.yww.utils.extension.settingIntent
import com.yww.utils.impl.IBrand

/**
 * @Author  WAVENING
 * @Date    2019/4/29-13:55
 */
internal class Samsung : IBrand.IManager {
    override val managerIntent: Intent = settingIntent()
}