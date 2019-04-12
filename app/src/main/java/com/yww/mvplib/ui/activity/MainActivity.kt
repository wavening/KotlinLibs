package com.yww.mvplib.ui.activity

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import com.yww.mvplib.R
import com.yww.mvplib.base.BaseActivity
import com.yww.mvplib.receiver.NetworkReceiver
import com.yww.mvplib.test.Test
import com.yww.mvplib.ui.contract.ContractMain
import com.yww.mvplib.ui.presenter.MainPresenter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<ContractMain.MainView, MainPresenter>(), ContractMain.MainView {

    private val networkReceiver = NetworkReceiver()
    override fun reCreatePresenter(): MainPresenter {
        return MainPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        app_main_tv_dialog.setOnClickListener {
            //            mPresenter.showMessage()
//            Test().log()
//            Test().startNextActivity(this@MainActivity)
//            Test().testSpManager()
            Test().testPermissionManager(this@MainActivity)
//            Test().testDownload()
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
//        registerReceiver(networkReceiver,filter)

    }

    override fun onPause() {
        super.onPause()
//        unregisterReceiver(networkReceiver)
    }

}
