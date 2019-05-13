package com.yww.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.Image
import android.media.ImageReader
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.app.FragmentManager
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import com.yww.base.BaseDialogFragment
import com.yww.camera.stragedy.CameraAction
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Semaphore

/**
 * @author  WAVENING
 */
const val cameraThreadName = "camera thread"

class Camera2DialogFragment : BaseDialogFragment(), CameraAction {
    private object Holder {
        val INSTANCE = Camera2DialogFragment()
    }

    companion object {
        @JvmStatic
        val instance: Camera2DialogFragment = Holder.INSTANCE
    }

    private var holder: CameraHolder = CameraHolder.instance
    private lateinit var cameraTexture: WeakReference<TextureView>
    private var cameraThread: HandlerThread = HandlerThread(cameraThreadName)
    private lateinit var cameraHandler: Handler
    private lateinit var optimalPreviewSize: Size
    private lateinit var optimalCaptureSize: Size
    private lateinit var requestBuilder: CaptureRequest.Builder
    private var cameraDevice: CameraDevice? = null
    private var cameraCaptureSession: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null
    private val cameraLock = Semaphore(1)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun getLayoutView(): View? {
        val textureView = TextureView(context)
        textureView.layoutParams = ViewGroup.LayoutParams(-1, -1)
        cameraTexture = WeakReference(textureView)
        return cameraTexture.get()
    }

    //开始预览
    override fun startPreview() {
        previewAsCameraDeviceOpened()
    }

    //停止预览
    override fun stopPreview() {
        cameraCaptureSession?.close()
        imageReader?.close()
        cameraDevice?.close()
    }

    //拍照
    override fun takePicture() {
        cameraLockFocus()
    }

    //关闭界面
    override fun close() {
        closeCameraPreview()
        dismissAllowingStateLoss()
    }

    //结束预览，关闭所有
    private fun closeCameraPreview() {
        stopPreview()
    }

    override fun onResume() {
        super.onResume()
        startCameraThread()
        when (cameraTexture.get()?.isAvailable) {
            true -> startPreview()
            false -> cameraTexture.get()?.surfaceTextureListener = surfaceListener
        }

    }

    override fun onPause() {
        closeCamera()
        stopCameraThread()
        super.onPause()
    }

    private fun closeCamera() {
        try {
            cameraLock.acquire()
            cameraCaptureSession?.close()
            cameraDevice?.close()
            cameraDevice = null
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {
            cameraLock.release()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar)
    }


    //拉起弹窗
    fun show(manger: FragmentManager) {
        if (showsDialog) {
            val transaction = fragmentManager?.beginTransaction()
            transaction?.remove(this)
            transaction?.commitAllowingStateLoss()
//            dismissAllowingStateLoss()
        }
        show(manger, "camera use dialog fragment")
    }

    //启动创建会话的子线程
    private fun startCameraThread() {
        if (!cameraThread.isAlive) {
            //创建并启动HandlerThread,原因是，创建会话的过程是耗时的过程，需要在子线程中完成创建，及相关初始化操作
            cameraThread.start()
            //创建线程的handler的looper使用线程的looper
            cameraHandler = Handler(cameraThread.looper)
        }
    }

    //暂停会话子线程
    private fun stopCameraThread() {
        if (cameraThread.isAlive) {
            //停止预览后需要将子线程关闭，以免下一次启动时报错
            cameraThread.interrupt()
        }
    }

    //创建TextureView 监听器
    private val surfaceListener: TextureView.SurfaceTextureListener = object : TextureView.SurfaceTextureListener {
        //第一步
        //当TextureView的SurfaceTexture可用时回调
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            //在SurfaceTexture可用时，
            // 第二步尝试开启摄像头
            tryOpenCamera(width, height)
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
            return true
        }
    }

    //第二步
    //尝试开启摄像头
    @SuppressLint("MissingPermission")
    private fun tryOpenCamera(width: Int, height: Int) {
        //获取CameraManager,这是打开Camera2的方法
        val manager = context?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        //第三步
        //开卡摄像头之前，可以选择设置相机的参数
        Log.e("camera open", "width=$width,height=$height")
        setupParameters(manager, width, height)
        //第四步
        //之后再开启摄像头
        manager.openCamera(holder.cameraId.toString(), deviceCallback, cameraHandler)
    }

    //第三步
    //设置Camera2相机参数
    private fun setupParameters(manager: CameraManager, width: Int, height: Int) {
        //遍历设备中所有摄像头
        val cameraIdList = manager.cameraIdList
        if (cameraIdList.isEmpty()) {
            holder.stateArray[CAMERA_NONE_ERROR]
        } else if (holder.cameraId.toString() !in cameraIdList) {
            holder.stateArray[CAMERA_ID_ERROR]
        }
        again@ for (cameraId in cameraIdList) {
            val characteristics = manager.getCameraCharacteristics(cameraId)
            //默认打开后置摄像头
            //在此处判断
            when (characteristics.get(CameraCharacteristics.LENS_FACING)) {
                CameraCharacteristics.LENS_FACING_FRONT -> continue@again
                else -> {
                    //获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
                    val map: StreamConfigurationMap =
                        characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                    //根据TextureView的尺寸设置预览尺寸
                    optimalPreviewSize =
                        selectOptimalPreviewSize(map.getOutputSizes(SurfaceTexture::class.java), width, height)
                    Log.e(
                        "camera preview",
                        "optimalPreviewSize==>width=${optimalPreviewSize.width},height=${optimalPreviewSize.height}"
                    )
                    //获取相机支持的最大拍照尺寸
                    optimalCaptureSize = selectOptimalPictureSize(map.getOutputSizes(ImageFormat.JPEG), width, height)
                    Log.e(
                        "camera picture",
                        "optimalCaptureSize==>width=${optimalCaptureSize.width},height=${optimalCaptureSize.height}"
                    )
                    //开启ImageReader,
                    tryOpenImageReader(optimalCaptureSize)
                    break@again
                }
            }
        }
    }

    //第四步
    //打开Camera2摄像头期间的回调
    private val deviceCallback =
        object : CameraDevice.StateCallback() {
            //当相机设备完成打开时调用的方法
            //此时，相机设备就可以使用了，可以调用CameraDevice.createCaptureSession()方法来设置第一个捕获会话。
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                //第五步
                //打开摄像头后设置预览
                previewAsCameraDeviceOpened()
            }

            //当相机设备不再可用时调用的方法
            //如果打开相机失败，可能会调用这个回调函数，
            // 而不会调用onOpened()方法。
            override fun onDisconnected(camera: CameraDevice) {
                cameraDevice?.close()//调用此方法后，不要再调用任何其他的方法，否则会抛出异常，
                cameraDevice = null
            }

            //当摄像机设备遇到严重错误时调用的方法。
            //如果打开相机失败，可能会调用这个回调函数，
            // 而不会调用onOpened()方法。
            override fun onError(camera: CameraDevice, error: Int) {
                cameraDevice?.close()
                cameraDevice = null
                dismissAllowingStateLoss()
            }
        }

    //第五步
    //开启预览
    private fun previewAsCameraDeviceOpened() {
        //第六步
        //在预览之前，必须要开启一个预览会话
        createCameraPreviewSession()
    }

    //第六步
    //创建相机预览会话
    private fun createCameraPreviewSession() {
        // 获取 texture 实例
        val tempTexture = cameraTexture.get()?.surfaceTexture!!
        // 设置 TextureView 缓冲区大小
        tempTexture.setDefaultBufferSize(optimalPreviewSize.width, optimalPreviewSize.height)
        // 获取 Surface 显示预览数据
        val tempSurface = Surface(tempTexture)
        // 构建适合相机预览的请求
        requestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)!!
        // 设置 surface 作为预览数据的显示界面
        requestBuilder.addTarget(tempSurface)
        //第七步
        // 创建相机捕获会话用于预览
        cameraDevice?.createCaptureSession(listOf(tempSurface, imageReader?.surface), sessionCallback, cameraHandler)
//        temSurface.release()
    }

    //第七步
    //创建会话回调
    private val sessionCallback = object : CameraCaptureSession.StateCallback() {
        //当摄像机设备完成了自身配置，会话可以开始处理捕获请求时，将调用此方法。
        //如果已经有捕获请求与会话一起排队，那么一旦调用这个回调，它们将开始处理，
        // 并且在调用这个回调之后，会话将立即调用 onActive()。
        override fun onConfigured(session: CameraCaptureSession) {
            assert(null != cameraDevice)
            //第八步
            //摄像机完成配置后，配置CaptureRequest ，在 onActive()之前
            configCaptureRequest(session)
        }

        //如果无法按要求配置会话，则调用此方法。
        //如果请求的输出集包含不支持的大小，或者一次请求太多输出，就会发生这种情况。
        override fun onConfigureFailed(session: CameraCaptureSession) {

        }
    }

    //第八步
    //配置预览界面，设置界面绘制
    private fun configCaptureRequest(session: CameraCaptureSession) {
        //在会话回调之后进行会话的保存，只允许其他地方调用，不允许其他地方修改
        cameraCaptureSession = session
        // 自动对焦
        requestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
        //captureBuilder,完成职责，进行保存，其他位置使用相同captureBuilder,先进行赋值
        session.setRepeatingRequest(requestBuilder.build(), null, cameraHandler)
    }


    //尝试开启图片读取
    private fun tryOpenImageReader(captureSize: Size) {
        imageReader = ImageReader.newInstance(captureSize.width, captureSize.height, ImageFormat.JPEG, 2)
        imageReader?.setOnImageAvailableListener(
            { reader -> cameraHandler.post { asyncSaver(reader.acquireNextImage()) } },
            cameraHandler
        )
    }

    //选择sizeMap中宽高比最接近width和height的size
    private fun selectOptimalPreviewSize(sizes: Array<Size>, width: Int, height: Int): Size {
        assert(!sizes.isNullOrEmpty())
        val list = mutableListOf<Size>()
        var diffA = width.toDouble()
        var diffB = width.toDouble()
        var tempSize = Size(width, height)
        var tempWidth: Int
        var tempHeight: Int
        for (size in sizes) {
            //Android设备的width>height
            Log.e("camera preview ", "size==>width=${size.width},height=${size.height}")
            //比较视图宽高，保证使用的宽高在比较时，与手机对宽高的定义相同
            //即width>height
            when {
                width >= height -> {
                    tempWidth = size.width
                    tempHeight = size.height
                }
                else -> {
                    tempWidth = size.height
                    tempHeight = size.width
                }
            }
            when (tempWidth == width && tempHeight == height) {
                //找到相同的size，直接使用
                true -> return size
                false -> when (tempWidth.toDouble() / tempHeight.toDouble() == width.toDouble() / height.toDouble()) {
                    true -> when {
                        tempWidth >= width -> when {
                            Math.abs(tempWidth.toDouble() / width.toDouble()) < diffA -> {
                                diffA = Math.abs(tempWidth.toDouble() / width.toDouble())
                                tempSize = size
                                Log.e(
                                    "camera preview",
                                    "  tempWidth >= width,满足条件diffA=$diffA,size=${size.width}*${size.height}"
                                )
                            }
                            else -> Unit
                        }
                        else -> when {
                            Math.abs(tempWidth.toDouble() / width.toDouble()) > diffA -> {
                                diffA = Math.abs(tempWidth.toDouble() / width.toDouble())
                                tempSize = size
                                Log.e(
                                    "camera preview",
                                    "  tempWidth <= width,满足条件diffA=$diffA,size=${size.width}*${size.height}"
                                )
                            }
                            else -> Unit
                        }
                    }
                    false -> when {
                        tempWidth >= width -> when {
                            Math.abs(tempWidth.toDouble() / width.toDouble()) < diffB -> {
                                diffB = Math.abs(tempWidth.toDouble() / width.toDouble())
                                list.add(size)
                                Log.e(
                                    "camera preview",
                                    "  tempWidth >= width,满足条件diffA=$diffB,size=${size.width}*${size.height}"
                                )
                            }
                            else -> Unit
                        }
                        else -> when {
                            Math.abs(tempWidth.toDouble() / width.toDouble()) > diffB -> {
                                diffB = Math.abs(tempWidth.toDouble() / width.toDouble())
                                list.add(size)
                                Log.e(
                                    "camera preview",
                                    "  tempWidth <= width,满足条件diffA=$diffB,size=${size.width}*${size.height}"
                                )
                            }
                            else -> Unit
                        }
                    }
                }

            }

        }//for end
        return when (tempSize) {
            Size(width, height) -> when (list.size) {
                0 -> {
                    Log.e("camera preview", "直接返回第一项")
                    sizes[0]
                }
                1 -> {
                    Log.e("camera preview", "唯一满足条件的Size")
                    list[0]
                }
                else -> {
                    Log.e("camera preview", "选择最接近的Size")
                    list.minBy { o1 -> Math.abs(o1.width * o1.height - width * height) }!!
                }
            }
            else -> tempSize
        }
    }

    //选择最适合的照片尺寸
    private fun selectOptimalPictureSize(sizes: Array<Size>, width: Int, height: Int): Size {
        assert(!sizes.isNullOrEmpty())
        val list = mutableListOf<Size>()
        var diffA = width.toDouble()
        var diffB = width.toDouble()
        var tempSize = Size(width, height)
        var tempWidth: Int
        var tempHeight: Int
        for (size in sizes) {
            //Android设备的width>height
            Log.e("camera picture ", "size==>width=${size.width},height=${size.height}")
            //比较视图宽高，保证使用的宽高在比较时，与手机对宽高的定义相同
            //即width>height
            when {
                width >= height -> {
                    tempWidth = size.width
                    tempHeight = size.height
                }
                else -> {
                    tempWidth = size.height
                    tempHeight = size.width
                }
            }
            when (tempWidth == width && tempHeight == height) {
                //找到相同的size，直接使用
                true -> return size
                false -> when (tempWidth.toDouble() / tempHeight.toDouble() == width.toDouble() / height.toDouble()) {
                    true -> when {
                        tempWidth >= width -> when {
                            Math.abs(tempWidth.toDouble() / width.toDouble()) < diffA -> {
                                diffA = Math.abs(tempWidth.toDouble() / width.toDouble())
                                tempSize = size
                                Log.e(
                                    "camera picture",
                                    "  tempWidth >= width,满足条件diffA=$diffA,size=${size.width}*${size.height}"
                                )
                            }
                            else -> Unit
                        }
                        else -> when {
                            Math.abs(tempWidth.toDouble() / width.toDouble()) > diffA -> {
                                diffA = Math.abs(tempWidth.toDouble() / width.toDouble())
                                tempSize = size
                                Log.e(
                                    "camera picture",
                                    "  tempWidth <= width,满足条件diffA=$diffA,size=${size.width}*${size.height}"
                                )
                            }
                            else -> Unit
                        }
                    }
                    false -> when {
                        tempWidth >= width -> when {
                            Math.abs(tempWidth.toDouble() / width.toDouble()) < diffB -> {
                                diffB = Math.abs(tempWidth.toDouble() / width.toDouble())
                                list.add(size)
                                Log.e(
                                    "camera picture",
                                    "  tempWidth >= width,满足条件diffA=$diffB,size=${size.width}*${size.height}"
                                )
                            }
                            else -> Unit
                        }
                        else -> when {
                            Math.abs(tempWidth.toDouble() / width.toDouble()) > diffB -> {
                                diffB = Math.abs(tempWidth.toDouble() / width.toDouble())
                                list.add(size)
                                Log.e(
                                    "camera picture",
                                    "  tempWidth <= width,满足条件diffA=$diffB,size=${size.width}*${size.height}"
                                )
                            }
                            else -> Unit
                        }
                    }
                }

            }

        }//for end
        return when (tempSize) {
            Size(width, height) -> when (list.size) {
                0 -> {
                    Log.e("camera picture", "直接返回第一项")
                    sizes[0]
                }
                1 -> {
                    Log.e("camera picture", "唯一满足条件的Size")
                    list[0]
                }
                else -> {
                    Log.e("camera picture", "选择最接近的Size")
                    list.minBy { o1 -> Math.abs(o1.width * o1.height - width * height) }!!
                }
            }
            else -> tempSize
        }
    }


    //保证预览以及焦点锁定
    private fun cameraLockFocus() {
        requestBuilder.set(
            CaptureRequest.CONTROL_AF_TRIGGER,
            CameraMetadata.CONTROL_AF_TRIGGER_START
        )
        cameraCaptureSession?.capture(
            requestBuilder.build(),
            captureCallbackPreTake,
            cameraHandler
        )
    }

    //解除预览以及焦点的锁定
    private fun cameraUnlockFocus() {
        val builder = requestBuilder
        builder.set(
            CaptureRequest.CONTROL_AF_TRIGGER,
            CameraMetadata.CONTROL_AF_TRIGGER_CANCEL
        )
        cameraCaptureSession?.setRepeatingRequest(requestBuilder.build(), null, cameraHandler)
    }

    //拍照前预览以及焦点锁定的回调
    private val captureCallbackPreTake: CameraCaptureSession.CaptureCallback =
        object : CameraCaptureSession.CaptureCallback() {
            //当图像捕获完全完成且所有结果元数据可用时，将调用此方法。
            override fun onCaptureCompleted(
                session: CameraCaptureSession,
                request: CaptureRequest, result: TotalCaptureResult
            ) {
                //图片捕获完成后,进行拍照，获取图片
                captureFullyPicture()
            }
        }

    //拍照，获取图片，并保存
    private fun captureFullyPicture() {
        val builder: CaptureRequest.Builder =
            cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)!!
        builder.addTarget(imageReader?.surface!!)
        builder.set(
            CaptureRequest.JPEG_ORIENTATION,
            holder.orientationArray[holder.cameraStrategy.cameraOrientation(context!!)]
        )
        cameraCaptureSession?.stopRepeating()
        cameraCaptureSession?.capture(
            requestBuilder.build(),
            captureCallbackPostTake,
            null
        )

    }

    //拍照后的回调
    private val captureCallbackPostTake = object : CameraCaptureSession.CaptureCallback() {
        //当图像捕获完全完成且所有结果元数据可用时，将调用此方法。
        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest, result: TotalCaptureResult
        ) {
            //图片拍摄完成后，解除预览以及焦点的锁定
            cameraUnlockFocus()
        }
    }

    private fun asyncSaver(image: Image): Runnable {
        return Runnable {
            val buffer = image.planes[0].buffer
            val data = ByteArray(buffer.remaining())
            buffer.get(data)
            val path = Environment.getExternalStorageDirectory().path + "/DCIM/CameraV2/"
            val imagePath = File(path)
            if (!imagePath.exists()) {
                imagePath.mkdir()
            }
            val timeStamp = SimpleDateFormat.getDateTimeInstance(0, 0).format(Date())
            val fileName = path + "IMG_" + timeStamp + ".jpg"
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(fileName)
                fos.write(data, 0, data.size)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    fos?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

    }
}