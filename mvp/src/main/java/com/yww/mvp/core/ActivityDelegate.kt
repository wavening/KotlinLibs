package com.yww.mvp.core

/**
 * @author  WAVENING
 */
internal interface ActivityDelegate {
    /**
     * 执行与Activity生命周期相符
     */
     fun onCreate()

    /**
     * 执行与Activity生命周期相符
     */
    fun onNewIntent()

    /**
     * 执行与Activity生命周期相符
     */
    fun onRestart()

    /**
     * 执行与Activity生命周期相符
     */
    fun onStart()

    /**
     * 执行与Activity生命周期相符
     */
    fun onResume()

    /**
     * 执行与Activity生命周期相符
     */
    fun onPause()

    /**
     * 执行与Activity生命周期相符
     */
    fun onStop()

    /**
     * 执行与Activity生命周期相符
     * @param remainInstance  是否保留数据
     */
    fun onDestroy(remainInstance: Boolean)
}