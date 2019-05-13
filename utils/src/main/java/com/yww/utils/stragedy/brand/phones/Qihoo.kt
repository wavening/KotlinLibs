package com.yww.utils.stragedy.brand.phones

import android.content.ComponentName
import android.content.Intent
import com.yww.utils.extension.packageName
import com.yww.utils.impl.IBrand

/**
 * @Author  WAVENING
 * @Date    2019/4/29-11:05
 */
internal class Qihoo : IBrand.IManager {
    private val action = "android.intent.action.MAIN"
    private val manager = "com.qihoo360.mobilesafe"
    private val managerMain = "com.qihoo360.mobilesafe.ui.index.AppEnterActivity"

    override val managerIntent: Intent by lazy {
        val intent = Intent(action)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", packageName)
        intent.component = ComponentName(manager, managerMain)
        intent
    }
}