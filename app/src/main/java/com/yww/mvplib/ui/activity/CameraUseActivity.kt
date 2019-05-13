package com.yww.mvplib.ui.activity

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.yww.camera.Camera2DialogFragment
import com.yww.mvplib.R
import com.yww.utils.manager.PermissionManager
import kotlinx.android.synthetic.main.activity_camera_use.*

/**
 * @author  WAVENING
 */
class CameraUseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_use)

        app_camera_use_camera2_preview_take.setOnClickListener {
            PermissionManager.instance
                .requestPermissionCallback(object : PermissionManager.PermissionCallback {
                    override fun onAllGranted(grantedPermissions: Set<String>) {
                        super.onAllGranted(grantedPermissions)
//                           startActivity<Camera2UsingActivity>()
                        Camera2DialogFragment.instance.show(supportFragmentManager)
                    }
                })
                .requestPermission(this@CameraUseActivity, setOf(Manifest.permission.CAMERA))
        }
    }
}