package com.yww.mvplib.ui.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import com.yww.base.BaseDialogFragment
import com.yww.mvplib.extension.indeterminateProgressDialog

/**
 * @author  WAVENING
 */

@SuppressLint("ValidFragment")
class LoadingDialogFragment private constructor() : BaseDialogFragment() {
    private object Holder {
        val INSTANCE = LoadingDialogFragment()
    }

    companion object {
        @JvmStatic
        val instance: LoadingDialogFragment by lazy { Holder.INSTANCE }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = indeterminateProgressDialog("正在努力加载页面", "请稍候")
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(true)
        return dialog
    }

    fun disturbDialogShowing() {
        dismissAllowingStateLoss()
    }
}