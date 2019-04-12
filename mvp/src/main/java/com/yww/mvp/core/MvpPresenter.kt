package com.yww.mvp.core


/**
 * @author  WAVENING
 */
interface MvpPresenter<V : MvpView> {
    /**
     * 上下文与 view绑定
     *
     * @param mvpView
     */
    fun attachView(mvpView: V)

    /**
     * 上下文与 view解绑
     *
     * @param remainInstance
     */
    fun detachView(remainInstance: Boolean)
}