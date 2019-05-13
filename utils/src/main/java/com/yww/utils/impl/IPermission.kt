package com.yww.utils.impl

import android.app.Activity
import android.support.annotation.Keep

/**
 * @Author  WAVENING
 * @Date    2019/4/12-15:09
 * PermissionStrategy 的策略接口
 */

@Keep
interface IPermission {
    /**
     * requestPermissions
     * @param activity INSTANCE of Activity
     * @param permissions permissions you request which are in the dangerous list
     */
    fun requestPermissions(activity: Activity, permissions: Set<String>)
}