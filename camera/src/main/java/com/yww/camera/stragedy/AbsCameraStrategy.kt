package com.yww.camera.stragedy

import android.content.Context

/**
 * @author  WAVENING
 */
abstract class AbsCameraStrategy {
    abstract fun cameraOrientation(context: Context?):Int
}