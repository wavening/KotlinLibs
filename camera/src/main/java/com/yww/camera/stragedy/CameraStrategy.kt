package com.yww.camera.stragedy

import android.app.Activity
import android.content.Context

/**
 * @author  WAVENING
 */
internal class CameraStrategy : AbsCameraStrategy() {
    override fun cameraOrientation(context: Context): Int {
        return when (context is Activity) {
            true -> context.windowManager.defaultDisplay.rotation
            false -> 0
        }
    }

}