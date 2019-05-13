package com.yww.mvplib.ui.fragment

import android.annotation.SuppressLint
import android.widget.Toast
import com.yww.mvplib.R
import com.yww.mvplib.base.BaseFragment
import kotlinx.android.synthetic.main.app_fragment_child_use_fragment.*

/**
 * @author  WAVENING
 */
@SuppressLint("ValidFragment")
class ChildUseFragment constructor(var name: String) : BaseFragment() {
    override var layoutId: Int = R.layout.app_fragment_child_use_fragment


    override fun onLazyLoaded() {
        super.onLazyLoaded()
        app_fragment_child_use_btn_first.text = name
        app_fragment_child_use_btn_first.setOnClickListener {
            Toast.makeText(context, "点击", Toast.LENGTH_SHORT).show()
        }
    }
}