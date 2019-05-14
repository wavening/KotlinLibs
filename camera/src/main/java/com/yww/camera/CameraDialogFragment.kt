package com.yww.camera

import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.Camera
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import com.yww.base.BaseDialogFragment
import com.yww.camera.stragedy.CameraAction
import java.lang.ref.WeakReference

/**
 * @author  WAVENING
 */
class CameraDialogFragment : BaseDialogFragment(), CameraAction {

    private object Holder {
        val INSTANCE = CameraDialogFragment()
    }

    companion object {
        @JvmStatic
        val instance: CameraDialogFragment = Holder.INSTANCE
    }

    private lateinit var cameraSurface: WeakReference<SurfaceView>
    private var camera: Camera? = null
    private var holder: CameraHolder = CameraHolder.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        if (context?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false)
            return

    }

    override fun getLayoutView(): View? {
        val surface = SurfaceView(context)
        surface.layoutParams = ViewGroup.LayoutParams(-1, -1)
        cameraSurface = WeakReference(surface)
        surface.holder.addCallback(holderCallback)
        return cameraSurface.get()
    }


    override fun startPreview() {
    }

    override fun stopPreview() {
    }

    //拍照
    override fun takePicture() {

    }

    override fun close() {
    }

    fun show(manager: FragmentManager) {
        if (showsDialog) {
            val transaction = fragmentManager?.beginTransaction()
            transaction?.remove(this)
            transaction?.commitAllowingStateLoss()
//            dismissAllowingStateLoss()
        }
        show(manager, "camera use dialog fragment")
    }

    private val holderCallback: SurfaceHolder.Callback2 = object : SurfaceHolder.Callback2 {
        override fun surfaceCreated(holder: SurfaceHolder?) {
            tryOpenCamera()
        }

        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            tryConfigCamera()
        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            tryDestroyCamera()
        }

        override fun surfaceRedrawNeeded(holder: SurfaceHolder?) {
        }
    }

    private val pictureCallback: Camera.PictureCallback = Camera.PictureCallback { data, _ ->
        asyncSaveImage(data)
    }


    //尝试打开摄像头
    private fun tryOpenCamera() {
        if (null == camera) {
            camera = Camera.open()
        }
        camera?.setPreviewDisplay(cameraSurface.get()?.holder)
        camera?.startPreview()
    }


    //摄像头参数设置
    private fun tryConfigCamera() {
        if (null == cameraSurface.get()?.holder?.surface) return
        if (null == camera) tryOpenCamera()
        camera?.stopPreview()
        camera?.setPreviewDisplay(cameraSurface.get()?.holder)
        setCameraParameters()
        camera?.startPreview()
    }

    private fun setCameraParameters() {
        //在config改变时，判断camera设备方向
        val orientation = holder.cameraStrategy.cameraOrientation(context)
        //设置预览方向
        camera?.setDisplayOrientation(selectOptimalDegrees(orientation))
        //获取摄像头原始参数
        val parameters = camera?.parameters!!
        //设置最合适的预览尺寸
        val optimalSize = parameters.supportedPreviewSizes?.let { selectOptimalSize(it, orientation) }
        optimalSize?.width?.let { parameters.setPreviewSize(it, optimalSize.height) }
        //设置最合适的图片尺寸(可以选择与预览一致)
        optimalSize?.width?.let { parameters.setPictureSize(it, optimalSize.height) }
        //设置预览的图片色彩样式
        parameters.previewFormat = ImageFormat.NV21
        //设置照片的图片色彩样式
        parameters.pictureFormat = ImageFormat.JPEG


        //设置相机fps
        val optimalFps = selectOptimalFps(parameters.supportedPreviewFpsRange)
        parameters.setPreviewFpsRange(optimalFps[0], optimalFps[1])

        //用于提高相机的fps帧率(从 开源中国 中摘抄)
        //3句全加，30fps
        parameters.setRecordingHint(true)//去掉这句12fps
        // parameters.setAutoExposureLock(true);//去掉这句，30fps
        // parameters.setAutoWhiteBalanceLock(true);//去掉这句，30fps

        //取消闪光灯
        parameters.focusMode = "off"
        //设置相机的白平衡
        parameters.whiteBalance = Camera.Parameters.WHITE_BALANCE_AUTO
        //设置相机的场景模式
        parameters.sceneMode = Camera.Parameters.SCENE_MODE_AUTO
        //设置相机的聚焦模式
        val focusModes = parameters.supportedFocusModes
        parameters.focusMode = when {
            focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) -> Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO) -> Camera.Parameters.FOCUS_MODE_AUTO
            else -> ""
        }
        //将新属性重新赋值到camera中
        camera?.parameters = parameters

    }

    //Camera销毁
    private fun tryDestroyCamera() {
        if (null != camera) {
            camera?.setPreviewDisplay(null)
            camera?.stopPreview()
            camera?.release()
            camera = null
        }
    }


    //选择最佳的预览视图旋转角度
    private fun selectOptimalDegrees(orientation: Int): Int {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(holder.cameraId, info)
        var degrees: Int = holder.orientationArray[orientation]
        return when (info.facing) {
            Camera.CameraInfo.CAMERA_FACING_FRONT -> {
                degrees = (info.orientation + degrees) % 360
                degrees = (360 - degrees) % 360
                degrees
            }
            else -> {
                degrees = (info.orientation - degrees + 360) % 360
                degrees
            }
        }

    }

    //选择最佳尺寸--得到与传入的宽高比最接近的size
    private fun selectOptimalSize(sizes: List<Camera.Size>, orientation: Int): Camera.Size {
        val width: Int
        val height: Int
        //根据设备的方向，确定宽高，确保与相机参数中的保持一致
        //(一般情况下，size.width >= size.height)
        if (1 == orientation || 3 == orientation) {
            width = cameraSurface.get()?.width!!
            height = cameraSurface.get()?.height!!
        } else {
            width = cameraSurface.get()?.height!!
            height = cameraSurface.get()?.width!!
        }
        var optimalSize: Camera.Size = camera?.Size(width, height)!!
        return when {
            sizes.isNullOrEmpty() -> optimalSize
            sizes.size == 1 -> sizes[0]
            else -> {
                val list: MutableList<Camera.Size> = mutableListOf()
                var delta =
                    Math.abs(width.toDouble() / height.toDouble() - sizes[0].width.toDouble() / sizes[0].height.toDouble())
                for (size in sizes) {
                    when (size.width == width && size.height == height) {
                        true -> return size
                        false -> when (size.width.toDouble() / size.height.toDouble() == width.toDouble() / height.toDouble()) {
                            true -> list.add(size)
                            false -> when {
                                Math.abs(width.toDouble() / height.toDouble() - size.width.toDouble() / size.height.toDouble()) < delta -> {
                                    delta =
                                        Math.abs(width.toDouble() / height.toDouble() - size.width.toDouble() / size.height.toDouble())
                                    optimalSize = size
                                }
                                else -> Unit
                            }
                        }
                    }
                }
                return when {
                    list.isNullOrEmpty() -> optimalSize
                    list.size == 1 -> list[0]
                    else -> list.minBy { Math.abs(width * height - it.width * it.height) }!!
                }
            }
        }
    }

    //选择最佳预览帧率
    private fun selectOptimalFps(ranges: List<IntArray>): IntArray {
        val list: MutableList<IntArray> = mutableListOf()
        return when {
            ranges.isNullOrEmpty() -> IntArray(1)
            ranges.size == 1 -> ranges[0]
            else -> {
                for (range in ranges) {
                    when {
                        range[0] == range[1] -> list.add(range)
                    }
                }
                return when {
                    list.isNullOrEmpty() -> IntArray(1)
                    list.size == 1 -> list[0]
                    else -> list.maxBy { it[0] * it[1] }!!
                }
            }
        }
    }

    //拍照，异步进行保存
    private fun takeAsyncPicture() {
        camera?.autoFocus { success, camera -> if (success) camera?.takePicture(null, null, pictureCallback) }
    }

    private fun asyncSaveImage(data: ByteArray) {

    }
}