package com.yww.camera.stragedy

import android.content.Context

/**
 * @author  WAVENING
 */
abstract class AbsCameraStragedy {
    abstract fun cameraOrientation(context: Context):Int
}