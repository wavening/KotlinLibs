package com.yww.mvplib.base

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * @author  WAVENING
 */
abstract class BaseDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var root: View? = getLayoutView()
        if (null == root) {
            if (0 == getLayoutId()) {
                return super.onCreateView(inflater, container, savedInstanceState)
            }
            root = inflater.inflate(getLayoutId(), null)
        }
        return root
    }


    protected open fun getLayoutView(): View? = null
    protected open fun getLayoutId(): Int = 0
}

