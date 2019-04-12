package com.yww.mvplib.ui.contract

import com.yww.mvplib.base.ContractBase


/**
 * @author  WAVENING
 */
interface ContractMain {
    interface MainView : ContractBase.BaseView

    interface MainPresenter : ContractBase.BasePresenter<MainView> {
        fun showMessage()
    }

    interface MainModel : ContractBase.BaseModel<MainListener> {
        fun achieveMessage()
    }

    interface MainListener : ContractBase.BaseListener {
        fun achieveMessageSuccess(message: CharSequence)
    }
}