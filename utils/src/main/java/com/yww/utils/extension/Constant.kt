package com.yww.utils.extension

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.support.annotation.StringRes
import com.yww.utils.util.Util

/**
 * @Author  WAVENING
 * @Date    2019/3/18-14:00
 */
@SuppressLint("InlinedApi")
const val calendarGroup: String = Manifest.permission_group.CALENDAR
@SuppressLint("InlinedApi")
const val cameraGroup: String = Manifest.permission_group.CAMERA
@SuppressLint("InlinedApi")
const val contactsGroup: String = Manifest.permission_group.CONTACTS
@SuppressLint("InlinedApi")
const val locationGroup: String = Manifest.permission_group.LOCATION
@SuppressLint("InlinedApi")
const val microphoneGroup: String = Manifest.permission_group.MICROPHONE
@SuppressLint("InlinedApi")
const val phoneGroup: String = Manifest.permission_group.PHONE
@SuppressLint("InlinedApi")
const val sensorGroup: String = Manifest.permission_group.SENSORS
@SuppressLint("InlinedApi")
const val smsGroup: String = Manifest.permission_group.SMS
@SuppressLint("InlinedApi")
const val storageGroup: String = Manifest.permission_group.STORAGE
const val undefinedGroup: String = ""

const val permissionRequestCode = 0x1111
const val threadName: String = "threadName"
@Suppress("UNUSED_EXPRESSION")
internal fun doInThreadLooper(expression: Any) {
    Thread({
        Looper.prepare()
        val handler = Handler(Looper.myLooper())
        Looper.loop()
        handler.post { expression }
    }, threadName)
}

internal fun getPermissionGroupDescriptionByStringId(@StringRes resId: Int): String = Util.getApplication()?.getString(resId)!!

internal fun openSettingActivity(packageName: String) {
    val intent = Intent()
    when (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
        true -> {
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.fromParts("package", packageName, null)
        }
        false -> {
            intent.action = Intent.ACTION_VIEW;
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra(
                when (Build.VERSION.SDK_INT == Build.VERSION_CODES.FROYO) {
                    true -> "pkg"
                    false -> "com.android.settings.ApplicationPkgName"
                }, packageName
            )
        }
    }
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    Util.getApplication()?.startActivity(intent)
}

internal val application: Application? = Util.getApplication()
internal val getPermissions: Set<String> = try {
    val pm: PackageManager? = Util.getApplication()?.packageManager
    pm?.getPackageInfo(Util.getApplication()?.packageName, PackageManager.GET_PERMISSIONS)
        ?.requestedPermissions?.toSet()!!
} catch (e: PackageManager.NameNotFoundException) {
    e.printStackTrace()
    emptySet()
} catch (e: Exception) {
    emptySet()

}

const val DOWNLOAD_NOTIFICATION_STYLE_0 = 0L
const val DOWNLOAD_NOTIFICATION_STYLE_1 = 1L
const val DOWNLOAD_NOTIFICATION_STYLE_2 = 2L
const val DOWNLOAD_NOTIFICATION_STYLE_3 = 3L

const val NETWORK_USAGE_STYLE_0 = 0L
const val NETWORK_USAGE_STYLE_1 = 1L
const val NETWORK_USAGE_STYLE_2 = 2L




