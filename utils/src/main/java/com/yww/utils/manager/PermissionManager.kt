package com.yww.utils.manager

import android.app.Activity
import android.support.annotation.Keep
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

@Keep
class PermissionManager {
    private object Holder {
        internal val INSTANCE = PermissionManager()
    }

    companion object {
        @Keep
        @JvmStatic
        val instance: PermissionManager = Holder.INSTANCE
        @Volatile
        @Keep
        @JvmStatic
        internal var permissionCallback: PermissionCallback = PermissionCallback.Callback()
        @Volatile
        @Keep
        @JvmStatic
        internal var reportCallbackEnable = false
        @Volatile
        @Keep
        @JvmStatic
        internal var reportCallback: ReportCallback = ReportCallback.Callback()
        @Volatile
        @Keep
        @JvmStatic
        internal var rationaleEnable = false
        @Volatile
        @Keep
        @JvmStatic
        internal var fullExtensionEnable = false
        @Volatile
        @Keep
        @JvmStatic
        private var strictModeEnable: Boolean = false

        @Keep
        @JvmStatic
        fun rationaleEnable(able: Boolean): Companion {
            this.rationaleEnable = able
            return this@Companion
        }

        @Keep
        @JvmStatic
        fun fullExtensionEnable(enable: Boolean): Companion {
            this.fullExtensionEnable = enable
            return this@Companion
        }

        @Keep
        @JvmStatic
        fun reportCallbackEnable(enable: Boolean): Companion {
            this.reportCallbackEnable = enable
            return this@Companion
        }

        @Keep
        @JvmStatic
        fun reportCallback(callback: ReportCallback): Companion {
            this.reportCallback = callback
            return this@Companion
        }

        @Keep
        @JvmStatic
        private fun strictModeEnable(enable: Boolean): Companion {
            this.strictModeEnable = enable
            return this@Companion
        }

        @Keep
        @JvmStatic
        internal fun reportPermissionProcessInfo(info: String) = when (reportCallbackEnable) {
            true -> reportCallback.reportPermissionInfo(info)
            false -> Unit
        }

        @Keep
        @JvmStatic
        fun permissionCallback(callback: PermissionCallback): Companion {
            permissionCallback = callback
            return this@Companion
        }

    }

    fun requestPermissionCallback(callback: PermissionCallback): PermissionManager {
        permissionCallback = callback
        return instance
    }

    @Keep
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
        if (activity is AppCompatActivity) {
            PermissionFragmentV4(permissions).show(activity.supportFragmentManager, "permission request")
        } else {
            PermissionFragment(permissions).show(activity.fragmentManager, "permission request")
        }
    }

    @Keep
    interface PermissionCallback {
        fun onCheckStarted() {}

        fun onGranted(grantedPermissions: Set<String>) {}

        fun onDenied(deniedPermissions: Set<String>, deniedForeverPermissions: Set<String>) {}

        fun onAllGranted(grantedPermissions: Set<String>) {}

        fun onAllDenied(grantedPermissions: Set<String>) {}

        fun onCheckFinished() {}

        class Callback : PermissionCallback
    }

    @Keep
    interface ReportCallback {
        fun reportPermissionInfo(info: String) {
            LogUtil.log(info)
        }

        class Callback : ReportCallback
    }


}