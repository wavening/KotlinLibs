package com.yww.mvplib.base

import com.yww.mvp.core.MvpPresenter
import com.yww.mvp.core.MvpView


/**
 * @author  WAVENING
 */
interface ContractBase {

    interface BaseView : MvpView {
        fun showLoading()

        fun hideLoading()

        fun showSuccess()

        fun showFail(message: CharSequence)
    }

    interface BasePresenter<V : BaseView> : MvpPresenter<V> {
        fun declareVariable()
    }

    interface BaseModel<L : BaseListener>

    interface BaseListener {
        fun showFail(message: CharSequence)
    }

}