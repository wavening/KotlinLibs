package com.yww.utils.manager

import android.app.Activity
import android.os.Build
import android.support.v7.app.AppCompatActivity
import com.yww.utils.extension.getPermissions
import com.yww.utils.util.LogUtil
import com.yww.utils.widget.PermissionFragment
import com.yww.utils.widget.PermissionFragmentV4

/**
 * @author  WAVENING
 * param explanation
 * INSTANCE ->  an INSTANCE of PermissionManager
 * permissionCallback -> callback bring info needed back to caller
 * requestRationale -> set true then if  the permissions you called are not all granted,show an request dialog to check it
 *                     set false then go well as usual
 * fullExtensionEnable -> set true then permission check will check all the permission belong to the permission_group
 *                        which the permission you request belong to
 * reportCallback -> log of permission check to report the process
 */

class PermissionManager {
    private object Holder {
        val INSTANCE = PermissionManager()

    }

    companion object {
        val instance: PermissionManager = Holder.INSTANCE
        @Volatile
        internal var permissionCallback: PermissionCallback = PermissionCallback.Callback()
        @Volatile
        internal var reportCallbackEnable = false
        @Volatile
        internal var reportCallback: ReportCallback = ReportCallback.Callback()
        @Volatile
        internal var rationaleEnable = false
        @Volatile
        internal var fullExtensionEnable = false
        @Volatile
        private var strictModeEnable: Boolean = false

        fun rationaleEnable(able: Boolean): PermissionManager.Companion {
            this.rationaleEnable = able
            return this@Companion
        }

        fun fullExtensionEnable(enable: Boolean): PermissionManager.Companion {
            this.fullExtensionEnable = enable
            return this@Companion
        }

        fun reportCallbackEnable(enable: Boolean): PermissionManager.Companion {
            this.reportCallbackEnable = enable
            return this@Companion
        }

        fun reportCallback(callback: ReportCallback): PermissionManager.Companion {
            this.reportCallback = callback
            return this@Companion
        }

        private fun strictModeEnable(enable: Boolean): PermissionManager.Companion {
            this.strictModeEnable = enable
            return this@Companion
        }

        internal fun reportPermissionProcessInfo(info: String) = when (reportCallbackEnable) {
            true -> reportCallback.reportPermissionInfo(info)
            false -> Unit
        }

        fun permissionCallback(callback: PermissionCallback): PermissionManager.Companion {
            permissionCallback = callback
            return this@Companion
        }

    }

    fun requestPermission(activity: Activity, permissions: Set<String>) {
        val iterator = permissions.iterator()
        while (iterator.hasNext()) {
            val permissionsInManifest = getPermissions
            val permission = iterator.next()
            when (permission in permissionsInManifest) {
                true -> Unit
                false -> return reportPermissionProcessInfo("$permission has not registered in manifest")
            }
        }

        LogUtil.log("activity==$activity")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Class.forName("android.support.v7.app.AppCompatActivity")
                Class.forName("android.support.v4.app.DialogFragment")
                if (activity is AppCompatActivity) {
                    PermissionFragmentV4(permissions).show(
                        activity.supportFragmentManager, "permission request"
                    )
                } else {
                    ClassNotFoundException("activity has not compile v7 jar ")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                PermissionFragment(permissions).show(
                    activity.fragmentManager,
                    "permission request"
                )
            }

        }
    }

    interface PermissionCallback {
        fun onCheckStarted() {}

        fun onGranted(grantedPermissions: Set<String>) {}

        fun onDenied(deniedPermissions: Set<String>, deniedForeverPermissions: Set<String>) {}

        fun allGranted() {}

        fun allDenied() {}

        fun onCheckFinished() {}

        class Callback : PermissionCallback
    }

    interface ReportCallback {
        fun reportPermissionInfo(info: String) {
            LogUtil.log(info)
        }

        class Callback : ReportCallback
    }


}