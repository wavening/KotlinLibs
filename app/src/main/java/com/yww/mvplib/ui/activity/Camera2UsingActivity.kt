package com.yww.mvplib.ui.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_camera2_using.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Camera2UsingActivity : AppCompatActivity() {

    private lateinit var mCameraId: String
    private lateinit var mPreviewSize: Size
    private lateinit var mCaptureSize: Size
    private lateinit var mCameraThread: HandlerThread
    private lateinit var mCameraHandler: Handler
    private lateinit var mCameraDevice: CameraDevice
    private lateinit var mImageReader: ImageReader
    private lateinit var mCaptureRequestBuilder: CaptureRequest.Builder
    private lateinit var mCaptureRequest: CaptureRequest
    private lateinit var mCameraCaptureSession: CameraCaptureSession
    private val orientationArray = SparseIntArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        orientationArray.append(Surface.ROTATION_0, 90)
        orientationArray.append(Surface.ROTATION_90, 0)
        orientationArray.append(Surface.ROTATION_180, 270)
        orientationArray.append(Surface.ROTATION_270, 180)
        //全屏无状态栏
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(com.yww.mvplib.R.layout.activity_camera2_using)
    }

    override fun onResume() {
        super.onResume()
        startCameraThread()
        if (!camera_texture_view.isAvailable) {
            camera_texture_view.surfaceTextureListener = mTextureListener
        } else {
            startPreview()
        }
    }

    private fun startCameraThread() {
        mCameraThread = HandlerThread("CameraThread")
        mCameraThread.start()
        mCameraHandler = Handler(mCameraThread.looper)
    }

    private val mTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            //当SurefaceTexture可用的时候，设置相机参数并打开相机
            setupCamera(width, height)
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return false
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

        }
    }

    private fun setupCamera(width: Int, height: Int) {
        //获取摄像头的管理者CameraManager
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            //遍历所有摄像头
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                //此处默认打开后置摄像头
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT)
                    continue
                //获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
                val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
//根据TextureView的尺寸设置预览尺寸
                mPreviewSize = getOptimalSize(map.getOutputSizes(SurfaceTexture::class.java), width, height)
                //获取相机支持的最大拍照尺寸
                mCaptureSize =
                    Collections.max(
                        map.getOutputSizes(ImageFormat.JPEG).toList()
                    ) { lhs, rhs -> java.lang.Long.signum((lhs.width * lhs.height - rhs.height * rhs.width).toLong()) }
                //此ImageReader用于拍照所需
                setupImageReader()
                mCameraId = cameraId
                break
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    //选择sizeMap中大于并且最接近width和height的size
    private fun getOptimalSize(sizeMap: Array<Size>, width: Int, height: Int): Size {
        val sizeList = mutableListOf<Size>()
        for (option in sizeMap) {
            if (width > height) {
                if (option.width > width && option.height > height) {
                    sizeList.add(option)
                }
            } else {
                if (option.width > height && option.height > width) {
                    sizeList.add(option)
                }
            }
        }
        return if (sizeList.size > 0) {
            Collections.min(sizeList, object : Comparator<Size> {
                override fun compare(lhs: Size, rhs: Size): Int {
                    return java.lang.Long.signum((lhs.width * lhs.height - rhs.width * rhs.height).toLong())
                }
            })
        } else sizeMap[0]
    }


    private fun openCamera() {
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            manager.openCamera(mCameraId, mStateCallback, mCameraHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    private val mStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            mCameraDevice = camera
            startPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            camera.close()
        }
    }

    private fun startPreview() {
        val mSurfaceTexture = camera_texture_view.surfaceTexture
        mSurfaceTexture.setDefaultBufferSize(mPreviewSize.width, mPreviewSize.height)
        val previewSurface = Surface(mSurfaceTexture)
        try {
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mCaptureRequestBuilder.addTarget(previewSurface)
            mCameraDevice.createCaptureSession(
                Arrays.asList(previewSurface, mImageReader.surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        try {
                            mCaptureRequest = mCaptureRequestBuilder.build()
                            mCameraCaptureSession = session
                            mCameraCaptureSession.setRepeatingRequest(mCaptureRequest, null, mCameraHandler)
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }

                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {

                    }
                },
                mCameraHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    fun takePicture(view: View) {
        lockFocus()
    }

    private fun lockFocus() {
        try {
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START)
            mCameraCaptureSession.capture(mCaptureRequestBuilder.build(), mCaptureCallback, mCameraHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    private val mCaptureCallback = object : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureProgressed(
            session: CameraCaptureSession,
            request: CaptureRequest,
            partialResult: CaptureResult
        ) {
        }

        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            capture()
        }
    }

    private fun capture() {
        try {
            val builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            val rotation = windowManager.defaultDisplay.rotation
            builder.addTarget(mImageReader.surface)
            builder.set(CaptureRequest.JPEG_ORIENTATION, orientationArray[rotation])
            val callback = object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {
                    Toast.makeText(applicationContext, "Image Saved!", Toast.LENGTH_SHORT).show()
                    unLockFocus()
                }
            }
            mCameraCaptureSession.stopRepeating()
            mCameraCaptureSession.capture(builder.build(), callback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    private fun unLockFocus() {
        try {
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL)
            //mCameraCaptureSession.capture(mCaptureRequestBuilder.build(), null, mCameraHandler);
            mCameraCaptureSession.setRepeatingRequest(mCaptureRequest, null, mCameraHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    override fun onPause() {
        super.onPause()
        mCameraCaptureSession.close()
        mCameraDevice.close()
        mImageReader.close()
    }

    private fun setupImageReader() {
        //2代表ImageReader中最多可以获取两帧图像流
        mImageReader = ImageReader.newInstance(
            mCaptureSize.width, mCaptureSize.height,
            ImageFormat.JPEG, 2
        )
        mImageReader.setOnImageAvailableListener(
            { reader -> mCameraHandler.post(asyncSaver(reader.acquireNextImage())) },
            mCameraHandler
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
