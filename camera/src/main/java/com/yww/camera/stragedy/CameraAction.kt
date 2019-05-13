package com.yww.camera.stragedy

/**
 * @author  WAVENING
 */
interface CameraAction {
    //开始预览
    fun startPreview()

    //停止预览
    fun stopPreview()

    //拍照
    fun takePicture()

    //关闭相机
    fun close()
}