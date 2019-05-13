package com.yww.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.util.AttributeSet
import android.view.TextureView
import android.view.View

/**
 * @author  WAVENING
 */

/**
 * 以5.0版本及以上为主
 */

@SuppressLint("MissingPermission")
class CameraView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes), TextureView.SurfaceTextureListener {
    private lateinit var holder: CameraHolder
    private val deviceCallback by lazy {
        object : CameraDevice.StateCallback() {
            //当相机设备完成打开时调用的方法
            //此时，相机设备就可以使用了，可以调用CameraDevice.createCaptureSession()方法来设置第一个捕获会话。
            override fun onOpened(camera: CameraDevice) {
                previewAsCameraDeviceOpened(camera)
            }

            //当相机设备不再可用时调用的方法
            //如果打开相机失败，可能会调用这个回调函数，
            // 而不会调用onOpened()方法。
            override fun onDisconnected(camera: CameraDevice) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            //当摄像机设备遇到严重错误时调用的方法。
            //如果打开相机失败，可能会调用这个回调函数，
            // 而不会调用onOpened()方法。
            override fun onError(camera: CameraDevice, error: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }

    private val sessionCallback by lazy {
        object : CameraCaptureSession.StateCallback() {
            //当摄像机设备完成了自身配置，会话可以开始处理捕获请求时，将调用此方法。
            //如果已经有捕获请求与会话一起排队，那么一旦调用这个回调，它们将开始处理，
            // 并且在调用这个回调之后，会话将立即调用 onActive()。
            override fun onConfigured(session: CameraCaptureSession) {
               //摄像机完成配置后，配置CaptureRequest ，在 onActive()之前
                configCaptureRequest(session)
            }
            //如果无法按要求配置会话，则调用此方法。
            //如果请求的输出集包含不支持的大小，或者一次请求太多输出，就会发生这种情况。
            override fun onConfigureFailed(session: CameraCaptureSession) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }

    private fun configCaptureRequest(session: CameraCaptureSession) {

    }

    fun prepareCameraHolder(holder: CameraHolder) {
        this.holder = holder
    }

    //一般，在确定拥有相机权限的情况下，调用初始化方法


    //当TextureView的SurfaceTexture可用时回调
    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //当TextureView的缓冲区大小时回调
    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //当通过SurfaceTexture.updateTexImage更新制定的TextureView时调用
    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //当制定的TextureView即将销毁时调用
    //返回true，在调用此方法后，surface texture 内部不应进行任何界面绘制的操作
    //返回false，客户端需手动调用SurfaceTexture.release()方法
    //多数app返回true
    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    //开启预览
    private fun previewAsCameraDeviceOpened(camera: CameraDevice) {
        //在预览之前，必须要开启一个预览会话
        createCameraSession(camera)
    }

    private fun createCameraSession(camera: CameraDevice) {
        camera.createCaptureSession(holder.surfaces, sessionCallback,)
    }

}