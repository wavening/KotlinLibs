package com.yww.mvp.core

/**
 * @author  WAVENING
 */
internal interface FragmentDelegate {
    fun onCreateView()

    fun onActivityCreated()

    fun onResume()

    fun onPause()

    fun onUserVisibleHint(isVisible: Boolean)

    fun onStop()

    fun onDestroyView()
}