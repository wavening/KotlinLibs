package com.yww.download

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.support.annotation.IntRange
import android.support.v4.util.ArrayMap
import java.util.concurrent.atomic.AtomicReference

/**
 * @Author  WAVENING
 * @Date    2019/3/21-16:32
 */
class DownloadHelper private constructor() {

    private object Holder {
        val INSTANCE = DownloadHelper()
        val REFERENCE: AtomicReference<DownloadManager> = AtomicReference()
    }

    companion object {
        val instance: DownloadHelper = Holder.INSTANCE
        private val downloadMap: ArrayMap<String, Long> = ArrayMap()
    }

    @Volatile
    private var downloadManager: DownloadManager? = null

    fun getDownloadManager(context: Context): DownloadManager {
        downloadManager = Holder.REFERENCE.get()
        return if (null != downloadManager) {
            downloadManager!!
        } else {
            downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            Holder.REFERENCE.compareAndSet(null, downloadManager)
            downloadManager!!
        }
    }

    fun download(
        downloadUrl: String, title: String, fileName: String, description: String,
        @IntRange(from = 0, to = 3) notifyStyle: Int, @IntRange(from = 0, to = 1) netStyle: Int
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
                else -> -1
            }
        )
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        request.setAllowedNetworkTypes(
            when (netStyle) {
                0 -> DownloadManager.Request.NETWORK_WIFI
                else -> DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI
            }
        )
        val enqueueId = downloadManager?.enqueue(request)!!
        downloadMap[downloadUrl] = enqueueId
        return enqueueId
    }

}