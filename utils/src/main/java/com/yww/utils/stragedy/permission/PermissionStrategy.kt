package com.yww.utils.stragedy.permission

import android.app.Activity
import android.support.annotation.Keep
import com.yww.utils.impl.IPermission
import com.yww.utils.util.LogUtil
import java.lang.ref.WeakReference

/**
 * @Author  WAVENING
 * @Date    2019/4/12-15:01
 */
@Keep
class PermissionStrategy private constructor() {

    private var reference: WeakReference<IPermission> = WeakReference(PermissionImpl.instance)

    private object Holder {
        val INSTANCE = PermissionStrategy()
    }

    companion object {
        @Keep
        @JvmStatic
        fun init(strategy: IPermission) {
            if (strategy != this.instance.reference.get())
                this.instance.reference = WeakReference(strategy)
        }

        @Keep
        @JvmStatic
        val instance: PermissionStrategy = Holder.INSTANCE
    }

    init {
        UnsupportedOperationException("please keep this singleton INSTANCE")
    }

    @Keep
    fun requestPermissions(activity: Activity, vararg permissions: String) {
        LogUtil.log("permissions==${permissions.toList()}")
        reference.get()?.requestPermissions(activity, permissions.toSet())
    }

    @Keep
    fun requestPermissions(activity: Activity, permissions: List<String>) {
        LogUtil.log("permissions==$permissions")
        reference.get()?.requestPermissions(activity, permissions.toSet())
    }

    @Keep
    fun requestPermissions(activity: Activity, permissions: Map<String, String>) {
        LogUtil.log("permissions==${permissions.toList()}")
        val permissionSet: MutableSet<String> = mutableSetOf()
        val iterator = permissions.iterator()
        while (iterator.hasNext()) {
            permissionSet.add(iterator.next().value)
        }
        reference.get()?.requestPermissions(activity, permissionSet)
    }

    @Keep
    fun requestPermissions(activity: Activity, permissions: Set<String>) {
        LogUtil.log("permissions==$permissions")
        reference.get()?.requestPermissions(activity, permissions)
    }
}