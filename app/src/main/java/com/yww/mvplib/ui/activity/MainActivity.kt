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
import org.jetbrains.anko.startActivity

class MainActivity : BaseActivity<ContractMain.MainView, MainPresenter>(), ContractMain.MainView {

    private val networkReceiver = NetworkReceiver()
    override fun reCreatePresenter(): MainPresenter {
        return MainPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        app_main_btn_permission.setOnClickListener {
            //            mPresenter.showMessage()
//            Test().log()
//            Test().startNextActivity(this@MainActivity)
//            Test().testSpManager()
            Test().testPermissionManager(this@MainActivity)
//            Test().testDownload()
//            Test().testOpenManager()
        }

        app_main_btn_camera.setOnClickListener {
            startActivity<CameraUseActivity>()
        }
        app_main_btn_fragment.setOnClickListener {
            startActivity<FragmentUseActivity>()
        }
        app_main_btn_dialog_fragment.setOnClickListener {
            startActivity<DialogFragmentActivity>()
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, filter)

    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkReceiver)
    }

}
