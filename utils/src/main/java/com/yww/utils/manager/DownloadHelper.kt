package com.yww.utils.manager

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.support.annotation.IntRange
import android.support.v4.util.ArrayMap
import com.yww.utils.extension.openSettingActivity
import com.yww.utils.util.PackageUtil
import com.yww.utils.util.Util

/**
 * @Author  WAVENING
 * @Date    2019/3/22-10:05
 */
class DownloadHelper private constructor() {
    private var downloadManager: DownloadManager =
        Util.getApplication()?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private val downloadMap: ArrayMap<String, Long> = ArrayMap()

    private object Holder {
        internal val INSTANCE = DownloadHelper()
    }

    companion object {
        val instance: DownloadHelper = Holder.INSTANCE
    }

    fun download(
        downloadUrl: String, title: String, fileName: String, description: String,
        @IntRange(from = 0, to = 3) notifyStyle: Int, @IntRange(from = 1, to = 3) netStyle: Int
    ): Long {
        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(downloadUrl))
        request.setTitle(title)
        request.setDescription(description)
        request.setNotificationVisibility(
            when (notifyStyle) {
                0 -> DownloadManager.Request.VISIBILITY_VISIBLE
                1 -> DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                2 -> DownloadManager.Request.VISIBILITY_HIDDEN
                3 -> DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION
                else -> DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            }
        )
        request.setVisibleInDownloadsUi(true)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        request.setAllowedNetworkTypes(
            when (netStyle) {
                1 -> DownloadManager.Request.NETWORK_MOBILE
                2 -> DownloadManager.Request.NETWORK_WIFI
                else -> DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI
            }
        )
        val enqueueId = downloadManager.enqueue(request)
        downloadMap[downloadUrl] = enqueueId
        return enqueueId
    }


    @SuppressLint("SwitchIntDef")
    fun downloadStateEnabled(): Boolean {
        return try {
            val state =
                PackageUtil.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads")
            return when (state) {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                        or PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                        or PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED
                -> false
                else -> true
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            false
        }

    }

    fun openDownloadComponentSetting() {
        openSettingActivity("com.android.providers.downloads")
    }
}