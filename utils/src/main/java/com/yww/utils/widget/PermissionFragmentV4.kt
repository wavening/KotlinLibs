package com.yww.utils.widget

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yww.utils.R
import com.yww.utils.annotation.PermissionConst
import com.yww.utils.extension.doInThreadLooper
import com.yww.utils.extension.openSettingActivity
import com.yww.utils.extension.permissionRequestCode
import com.yww.utils.manager.PermissionManager
import com.yww.utils.util.Util

/**
 * @Author  WAVENING
 * @Date    2019/3/19-13:36
 */
@SuppressLint("ValidFragment")
@RequiresApi(api = Build.VERSION_CODES.M)
internal class PermissionFragmentV4(private val permissionsRequest: Set<String>) : DialogFragment() {
    /**
     *  before permission check ,first check permission state
     */
    private val checkedGrantedPermissions: MutableSet<String> = mutableSetOf()
    private val checkedDeniedPermissions: MutableSet<String> = mutableSetOf()
    private val checkedDeniedForeverPermissions: MutableSet<String> = mutableSetOf()
    /**
     * after permission check, result check permission  state
     */
    private val grantedPermissions: MutableSet<String> = mutableSetOf()
    private val deniedPermissions: MutableSet<String> = mutableSetOf()
    private val deniedForeverPermissions: MutableSet<String> = mutableSetOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionManager.permissionCallback.onCheckStarted()
        reportInfo("start to check permissions")
        val iterator = permissionsRequest.iterator()
        while (iterator.hasNext()) {
            val permission = iterator.next()
            when (checkSelfPermissionGranted(permission)) {
                true -> checkedGrantedPermissions.add(permission)
                false -> checkedDeniedPermissions.add(permission)
            }
        }
        reportInfo(
            "first check permissions =>" +
                    " \n granted -> $checkedGrantedPermissions" +
                    " \n denied -> $checkedDeniedPermissions"
        )
        when (checkedDeniedPermissions.isNullOrEmpty()) {
            true -> doWhenAllPermissionRequestGrantedDirectly()
            false -> {
                checkedDeniedPermissions.forEach {
                    when (shouldShowPermissionRationale(it)) {
                        true -> Unit
                        false -> checkedDeniedForeverPermissions.add(it)
                    }
                }
                when (checkedDeniedPermissions.isNullOrEmpty()) {
                    true -> Unit
                    false -> requestPermissionWhenRationaleFalse(checkedDeniedPermissions)
                }
            }
        }

    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window.setDimAmount(0f)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (permissionRequestCode == requestCode && permissions.size == grantResults.size) {
            var i = 0
            while (i < grantResults.size) {
                when (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    true -> grantedPermissions.add(permissions[i])
                    false -> {
                        deniedPermissions.add(permissions[i])
                        when (shouldShowPermissionRationale(permissions[i])) {
                            true -> Unit
                            false -> deniedForeverPermissions.add(permissions[i])
                        }
                    }
                }
                i++
            }
            doWhenAllPermissionsHandled(permissions.toSet())
        }
    }

    private fun doWhenAllPermissionsHandled(permissions: Set<String>) {
        PermissionManager.permissionCallback.onGranted(grantedPermissions)
        PermissionManager.permissionCallback.onDenied(deniedPermissions, deniedForeverPermissions)
        when (grantedPermissions == permissions) {
            true -> doWhenAllPermissionRequestGranted()
            false -> doWhenHasPermissionsRequestDenied()
        }
    }

    private fun checkSelfPermissionGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(activity!!, permission) == PackageManager.PERMISSION_GRANTED

    private fun shouldShowPermissionRationale(deniedPermission: String): Boolean =
        ActivityCompat.shouldShowRequestPermissionRationale(activity!!, deniedPermission)

    private fun doWhenHasPermissionsRequestDenied() {
        reportInfo("permissions denied => $deniedPermissions")
        when (deniedPermissions == when (PermissionManager.fullExtensionEnable) {
            true -> PermissionConst.instance.getGroupPermissionsByPermissionName(permissionsRequest)
            false -> permissionsRequest
        }) {
            true -> PermissionManager.permissionCallback.allDenied()
            false -> Unit
        }
        when (PermissionManager.rationaleEnable) {
            true -> showRequestRationaleDialog(deniedPermissions)
            false -> dismissAllowingStateLoss()
        }
    }

    private fun doWhenAllPermissionRequestGrantedDirectly() {
        reportInfo("all permissions granted directly")
        doWhenAllPermissionRequestGranted()
    }

    private fun doWhenAllPermissionRequestGranted() {
        reportInfo("all permissions granted")
        PermissionManager.permissionCallback.allGranted()
        dismissAllowingStateLoss()
    }

    private fun showRequestRationaleDialog(permissions: Set<String>) {
        reportInfo("show rationale dialog")
        doInThreadLooper(createRationaleDialog(permissions))
    }

    private fun requestPermissionWhenRationaleFalse(permissions: Set<String>) {
        reportInfo("request permissions for denied permissions => $permissions")
        doInThreadLooper(findExactPermissions(permissions))
    }

    private fun findExactPermissions(permissions: Set<String>) {
        val allPermissions: Set<String> = when (PermissionManager.fullExtensionEnable) {
            true -> PermissionConst.instance.getGroupPermissionsByPermissionName(permissions)
            false -> permissions
        }
        reportInfo("request permissions for denied permissions actually => $allPermissions")
        requestPermissions(allPermissions.toTypedArray(), permissionRequestCode)
    }

    @Suppress("NAME_SHADOWING")
    private fun createRationaleDialog(permissions: Set<String>) {
        val permission: String = getString(R.string.text_dialog_content_permission_request) +
                PermissionConst.instance.getPermissionDescription(permissions).toString()
        val builder = AlertDialog.Builder(activity, android.R.style.Theme_Material_Light_Dialog)
            .setTitle(R.string.text_dialog_title_permission_request)
            .setMessage(permission)
            .setPositiveButton(R.string.text_dialog_positive_permission_request) { _, _ ->
                dismissAllowingStateLoss()
                launchSettingActivity()
            }
            .setNegativeButton(R.string.text_dialog_negative_permission_request) { _, _ -> dismissAllowingStateLoss() }
            .setCancelable(true)
        val dialog = builder.create()
        dialog.setInverseBackgroundForced(true)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun launchSettingActivity() {
        openSettingActivity(Util.getApplication()?.packageName!!)
    }

    private fun reportInfo(info: String) = PermissionManager.reportPermissionProcessInfo(info)

    override fun dismiss() {
        PermissionManager.permissionCallback.onCheckFinished()
        super.dismiss()
    }

    override fun dismissAllowingStateLoss() {
        PermissionManager.permissionCallback.onCheckFinished()
        super.dismissAllowingStateLoss()
    }


}