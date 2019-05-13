package com.yww.mvplib.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * @author  WAVENING
 */
abstract class BaseFragment : Fragment() {

    private var prepared = false
    private var lazyLoaded = false
    protected open lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(layoutId, container,false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prepared = true
        lazyLoad()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        lazyLoad()
    }

    private fun lazyLoad() {
        if (userVisibleHint && prepared && !lazyLoaded) {
            lazyLoaded = true
            onLazyLoaded()
        }

    }

    protected abstract var layoutId: Int

    protected open fun onLazyLoaded() {}
}