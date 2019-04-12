@file:Suppress("NOTHING_TO_INLINE")

package com.yww.mvplib.extension


/**
 * @author  WAVENING
 */
import android.app.ProgressDialog
import android.content.Context
import android.support.v4.app.DialogFragment

fun DialogFragment.indeterminateProgressDialog(
    message: String? = null,
    title: String? = null,
    init: (ProgressDialog.() -> Unit)? = null
) = context!!.indeterminateProgressDialog(message, title, init)


fun Context.indeterminateProgressDialog(
    message: String? = null,
    title: String? = null,
    init: (ProgressDialog.() -> Unit)? = null
) = progressDialog(true, message, title, init)

private fun Context.progressDialog(
    indeterminate: Boolean,
    message: String? = null,
    title: String? = null,
    init: (ProgressDialog.() -> Unit)? = null
) = ProgressDialog(this).apply {
    isIndeterminate = indeterminate
    if (!indeterminate) setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
    if (message != null) setMessage(message)
    if (title != null) setTitle(title)
    if (init != null) init()
    show()
}