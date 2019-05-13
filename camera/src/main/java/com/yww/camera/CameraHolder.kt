package com.yww.camera

import android.util.ArrayMap
import android.util.SparseIntArray
import android.view.Surface
import com.yww.camera.stragedy.AbsCameraStrategy
import com.yww.camera.stragedy.CameraStrategy

/**
 * @author  WAVENING
 */
const val CAMERA_NONE_ERROR = 1
const val CAMERA_ID_ERROR = 2

class CameraHolder private constructor() {

    private object Holder {
        val INSTANCE = CameraHolder()
    }

    companion object {
        val instance = Holder.INSTANCE
    }

    val orientationArray = SparseIntArray()
    var cameraStrategy: AbsCameraStrategy = CameraStrategy()
    var stateArray: ArrayMap<Int, CameraSate> = ArrayMap()

    init {
        orientationArray.append(Surface.ROTATION_0, 0)
        orientationArray.append(Surface.ROTATION_90, 90)
        orientationArray.append(Surface.ROTATION_180, 180)
        orientationArray.append(Surface.ROTATION_270, 270)

        stateArray[CAMERA_NONE_ERROR] = CameraSate.ERROR_NONE
        stateArray[CAMERA_ID_ERROR] = CameraSate.ERROR_ID
    }

    var surfaces: MutableList<Surface> = mutableListOf()
    var cameraId: Int = 0


}
