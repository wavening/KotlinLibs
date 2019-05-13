package com.yww.utils.stragedy.brand.phones

import android.content.ComponentName
import android.content.Intent
import com.yww.utils.extension.packageName
import com.yww.utils.extension.settingIntent
import com.yww.utils.impl.IBrand
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * @Author  WAVENING
 * @Date    2019/4/25-18:05
 */
internal class Xiaomi : IBrand.IManager {
    private val extraname = ""
    private val xiaomiAction = "miui.intent.action.APP_PERM_EDITOR"
    private val manager = "com.miui.securitycenter"
    private val managerMainApp = "com.miui.permcenter.permissions.AppPermissionsEditorActivity"
    private val managerMain = "com.miui.permcenter.permissions.PermissionsEditorActivity"
    private val MIUI_V6 = "V6"
    private val MIUI_V7 = "V7"
    private val MIUI_V8 = "V8"
    private val MIUI_V9 = "V9"

    override val managerIntent: Intent by lazy {
        when (getMiuiVersion()) {
            MIUI_V6 -> packV6V7
            MIUI_V7 -> packV6V7
            MIUI_V8 -> packV8V9
            MIUI_V9 -> packV8V9
            else -> settingIntent()
        }
    }

    private val packV6V7: Intent by lazy {
        val intent = Intent()
        intent.action = xiaomiAction
        intent.component = ComponentName(manager, managerMainApp)
        intent.putExtra("extra_pkgname", packageName)
        intent
    }
    private val packV8V9: Intent by lazy {
        val intent = Intent()
        intent.action = xiaomiAction
        intent.component = ComponentName(manager, managerMain)
        intent.putExtra("extra_pkgname", packageName)
        intent
    }


    private fun getMiuiVersion(): String {
        val propName = "ro.miui.ui.version.name"
        val line: String
        var input: BufferedReader? = null
        try {
            val p: Process = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        } finally {
            try {
                input?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return line
    }
}