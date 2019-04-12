package com.yww.utils

import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.yww.utils.manager.DownloadHelper
import com.yww.utils.stragedy.permission.PermissionStrategy
import com.yww.utils.util.PackageUtil

/**
 * @author  WAVENING
 */
object TestUtils {
    fun test1(activity: AppCompatActivity) {
        PermissionStrategy.instance
            .requestPermissions(
                activity,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_PHONE_STATE
            )
        Log.e("utils", "${BuildConfig.APPLICATION_ID}\n${BuildConfig.VERSION_NAME}")
        Log.e("utils", "package util " + PackageUtil.getPackageManager())
        Log.e("utils", "package util " + PackageUtil.getPackageManager())
    }

    fun testDownloadSetting() {
//        DownloadHelper.INSTANCE.openDownloadComponentSetting()
        DownloadHelper.instance.download(
            "http://pic24.nipic.com/20120906/2786001_082828452000_2.jpg"
            , "scene", "scene.jpg", "风景图片", 1, 1
        )
    }
}