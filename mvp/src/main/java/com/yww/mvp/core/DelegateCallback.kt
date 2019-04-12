package com.yww.mvp.core

/**
 * @author  WAVENING
 */
internal interface DelegateCallback<V : MvpView, P : MvpPresenter<V>> {
    /**
     * 创建对应的presenter
     *
     * @return P
     */
    fun createPresenter(): P

    /**
     * 获取当前上下文的presenter
     *
     * @return P
     */
    fun getPresenter(): P

    /**
     * 设置presenter 使之与上下文相关
     */
    fun setNewPresenter(presenter: P)

    /**
     * 获取当前上下文的view
     *
     * @return V
     */
    fun getMvpView(): V

}