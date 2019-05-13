package com.yww.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.Image
import android.media.ImageReader
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.util.Size
import android.view.Surface
import android.view.TextureView
import com.yww.camera.stragedy.CameraAction
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author  WAVENING
 */
const val threadName = "CameraThread"

@SuppressLint("MissingPermission")
class CameraTextureView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : TextureView(context, attrs, defStyleAttr, defStyleRes), TextureView.SurfaceTextureListener
    , CameraAction {

    private val mainThread = HandlerThread(threadName)
    private var mainHandler: Handler
    private lateinit var holder: CameraHolder
    private lateinit var optimalPreviewSize: Size
    private lateinit var optimalCaptureSize: Size
    private lateinit var cameraDevice: CameraDevice
    private lateinit var captureBuilder: CaptureRequest.Builder
    private lateinit var cameraCaptureSession: CameraCaptureSession
    private lateinit var imageReader: ImageReader

    //一般，在确定拥有相机权限的情况下，调用初始化方法
    init {
        //初始化时创建HandlerThread,原因是，创建会话的过程是耗时的过程，需要在子线程中完成创建，及相关初始化操作
        mainThread.start()
        //创建线程的handler的looper使用线程的looper
        mainHandler = Handler(mainThread.looper)
    }

    fun prepareCameraHolder(holder: CameraHolder) {
        this.holder = holder
    }

    override fun takePicture() {
        //拍照时保证焦点锁定
        cameraLockFocus()
    }

    override fun startPreview() {
        //开启预览
        previewAsCameraDeviceOpened()
    }

    override fun stopPreview() {
        //停止预览
        cameraUnlockFocus()
    }

    override fun close() {
        cameraCaptureSession.close()
        cameraDevice.close()
        imageReader.close()
    }

    //第一步
    //当TextureView的SurfaceTexture可用时回调
    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        //在SurfaceTexture可用时，
        // 第二步尝试开启摄像头
        tryOpenCamera(width, height)
    }

    //当TextureView的缓冲区大小时回调
    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
    }

    //当通过SurfaceTexture.updateTexImage更新指定的TextureView时调用
    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    //当制定的TextureView即将销毁时调用
//返回true，在调用此方法后，surface texture 内部不应进行任何界面绘制的操作
//返回false，客户端需手动调用SurfaceTexture.release()方法
//多数app返回true
    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        return false
    }


    //第二步
    //尝试开启摄像头
    private fun tryOpenCamera(width: Int, height: Int) {
        //获取CameraManager,这是打开Camera2的方法
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        //第三步
        //开卡摄像头之前，可以选择设置相机的参数
        setupParameters(manager, width, height)
        //第四步
        //之后再开启摄像头
        manager.openCamera(holder.cameraId.toString(), deviceCallback, mainHandler)
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
                    //获取相机支持的最大拍照尺寸
                    optimalCaptureSize = Collections.max(map.getOutputSizes(ImageFormat.JPEG).toMutableList())
                    { o1, o2 -> Long.SIZE_BITS.compareTo(o1.width * o1.height - o2.width * o2.height) }

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
                camera.close()//调用此方法后，不要再调用任何其他的方法，否则会抛出异常，
            }

            //当摄像机设备遇到严重错误时调用的方法。
            //如果打开相机失败，可能会调用这个回调函数，
            // 而不会调用onOpened()方法。
            override fun onError(camera: CameraDevice, error: Int) {
                camera.close()
            }
        }


    //选择sizeMap中大于并且最接近width和height的size
    private fun selectOptimalPreviewSize(sizes: Array<Size>, width: Int, height: Int): Size {
        val list: MutableList<Size> = mutableListOf()
        for (size in sizes) {
            when (width > height) {
                true -> when (size.width > width && size.height > height) {
                    true -> list.add(size)
                    false -> Unit
                }
                false -> when (size.width > height && size.height > width) {
                    true -> list.add(size)
                    false -> Unit
                }
            }
        }
        return when (list.size > 0) {
            true -> Collections.min(list) { o1, o2 -> Long.SIZE_BITS.compareTo(o1.width * o1.height - o2.width * o2.height) }
            false -> sizes[0]
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
        val temTexture = surfaceTexture
        temTexture.setDefaultBufferSize(optimalPreviewSize.width, optimalPreviewSize.height)
        val temSurface = Surface(temTexture)
        val builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        builder.addTarget(temSurface)
        //第七步
        cameraDevice.createCaptureSession(listOf(temSurface, imageReader.surface), sessionCallback, mainHandler)
//        temSurface.release()
    }

    //第七步
    //创建会话回调
    private val sessionCallback = object : CameraCaptureSession.StateCallback() {
        //当摄像机设备完成了自身配置，会话可以开始处理捕获请求时，将调用此方法。
        //如果已经有捕获请求与会话一起排队，那么一旦调用这个回调，它们将开始处理，
        // 并且在调用这个回调之后，会话将立即调用 onActive()。
        override fun onConfigured(session: CameraCaptureSession) {
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
        //captureBuilder,完成职责，进行保存，其他位置使用相同captureBuilder,先进行赋值
        session.setRepeatingRequest(captureBuilder.build(), null, mainHandler)
    }

    //保证预览以及焦点锁定
    private fun cameraLockFocus() {
        captureBuilder.set(
            CaptureRequest.CONTROL_AF_TRIGGER,
            CameraMetadata.CONTROL_AF_TRIGGER_START
        )
        cameraCaptureSession.capture(
            captureBuilder.build(),
            captureCallbackPreTake,
            mainHandler
        )
    }

    //解除预览以及焦点的锁定
    private fun cameraUnlockFocus() {
        val builder = captureBuilder
        builder.set(
            CaptureRequest.CONTROL_AF_TRIGGER,
            CameraMetadata.CONTROL_AF_TRIGGER_CANCEL
        )
        cameraCaptureSession.setRepeatingRequest(captureBuilder.build(), null, mainHandler)
    }

    //拍照前预览以及焦点锁定的回调
    private val captureCallbackPreTake = object : CameraCaptureSession.CaptureCallback() {
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
            cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        builder.addTarget(imageReader.surface)
        builder.set(
            CaptureRequest.JPEG_ORIENTATION,
            holder.orientationArray[holder.cameraStrategy.cameraOrientation(context)]
        )
        cameraCaptureSession.stopRepeating()
        cameraCaptureSession.capture(
            captureBuilder.build(),
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


    //尝试开启图片读取
    private fun tryOpenImageReader(captureSize: Size) {
        imageReader = ImageReader.newInstance(captureSize.width, captureSize.height, ImageFormat.JPEG, 2)
        imageReader.setOnImageAvailableListener(
            { reader -> mainHandler.post { asyncSaver(reader.acquireNextImage()) } },
            mainHandler
        )
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