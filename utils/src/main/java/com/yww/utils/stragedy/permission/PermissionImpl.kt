package com.yww.utils.stragedy.permission

import android.app.Activity
import com.yww.utils.BuildConfig
import com.yww.utils.impl.IPermission
import com.yww.utils.manager.PermissionManager
import com.yww.utils.util.LogUtil

/**
 * @Author  WAVENING
 * @Date    2019/4/12-15:12
 */
internal class PermissionImpl : IPermission {
    private object Holder {
        val INSTANCE = PermissionImpl()
    }

    companion object {
        init {
            PermissionManager.rationaleEnable(BuildConfig.DEBUG)
                .fullExtensionEnable(BuildConfig.DEBUG)
                .reportCallbackEnable(BuildConfig.DEBUG)
                .permissionCallback(PermissionManager.PermissionCallback.Callback())
        }

        val instance: PermissionImpl = Holder.INSTANCE
    }

    override fun requestPermissions(activity: Activity, permissions: Set<String>) {
        LogUtil.log("permissions==$permissions")
        PermissionManager.instance
            .requestPermission(activity, permissions)
    }
}