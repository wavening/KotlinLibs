package com.yww.mvplib.base

import com.yww.mvp.BaseMvpPresenter

/**
 * @author  WAVENING
 */
abstract class BasePresenter<V : ContractBase.BaseView> :
    BaseMvpPresenter<V>(), ContractBase.BasePresenter<V>, ContractBase.BaseListener{
    protected lateinit var view: V

    override fun declareVariable() {
        view = getMvpView() as V
    }

    override fun showFail(message: CharSequence) {
        view.hideLoading()
        view.showFail(message)
    }
}