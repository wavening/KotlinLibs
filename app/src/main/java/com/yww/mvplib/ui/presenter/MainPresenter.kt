package com.yww.mvplib.ui.presenter

import com.yww.mvplib.base.BasePresenter
import com.yww.mvplib.ui.contract.ContractMain
import com.yww.mvplib.ui.model.MainModel

/**
 * @author  WAVENING
 */
class MainPresenter : BasePresenter<ContractMain.MainView>(), ContractMain.MainPresenter,
    ContractMain.MainListener {
    private var model: MainModel = MainModel(this@MainPresenter)

    override fun showMessage() {
        view.showLoading()
        model.achieveMessage()
    }


    override fun achieveMessageSuccess(message: CharSequence) {
        showFail(message)
    }

}
