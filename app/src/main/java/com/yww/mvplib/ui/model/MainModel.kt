package com.yww.mvplib.ui.model

import com.yww.mvplib.base.BaseModel
import com.yww.mvplib.ui.contract.ContractMain

/**
 * @author  WAVENING
 */
class MainModel(listener: ContractMain.MainListener) : BaseModel<ContractMain.MainListener>(listener),ContractMain.MainModel {
    override fun achieveMessage() {
        listener.achieveMessageSuccess("welcome to main and congratulations")
    }
}