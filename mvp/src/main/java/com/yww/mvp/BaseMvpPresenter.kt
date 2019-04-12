package com.yww.mvp

import com.yww.mvp.core.MvpPresenter
import com.yww.mvp.core.MvpView
import java.lang.ref.WeakReference

/**
 * @author  WAVENING
 */
abstract class BaseMvpPresenter<V : MvpView> : MvpPresenter<V> {
    private lateinit var weakRef: WeakReference<V>
    override fun attachView(mvpView: V) {
        weakRef = WeakReference(mvpView)
    }

    override fun detachView(remainInstance: Boolean) {
        weakRef.clear()
    }

    fun getMvpView(): V? =
        if (isViewAttached()) weakRef.get() else throw NullPointerException("have you ever called attachView() in BasePresenter")


    private fun isViewAttached(): Boolean {
        return null != weakRef.get()
    }
}