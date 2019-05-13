package com.yww.camera

/**
 * @author  WAVENING
 */
enum class CameraSate(val message: String) {
    //CAMERA_NONE_ERROR
    ERROR_NONE("设备中未找到相机"),
    //CAMERA_ID_ERROR
    ERROR_ID("未找到对应的相机"),
}