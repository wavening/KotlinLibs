package com.yww.mvp.core

/**
 * @author  WAVENING
 */
internal class MvpInternalDelegate<V : MvpView, P : MvpPresenter<V>>(private var callback: DelegateCallback<V, P>) {
    /**
     * 创建presenter
     */
    fun createPresenter(): P = callback.getPresenter()


    /**
     * 绑定视图
     */
    fun attachView() {
        callback.getPresenter().attachView(callback.getMvpView())
    }

    /**
     * 解绑视图
     */
    fun detachView(remainInstance: Boolean) {
        callback.getPresenter().detachView(remainInstance)
    }
}