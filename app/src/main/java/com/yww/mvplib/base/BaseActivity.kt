package com.yww.mvplib.base

import android.os.Bundle
import com.yww.mvp.BaseMvpActivity
import com.yww.mvplib.ui.dialog.LoadingDialogFragment
import org.jetbrains.anko.toast


/**
 * @author  WAVENING
 */
abstract class BaseActivity<V : ContractBase.BaseView, P : ContractBase.BasePresenter<V>> :
    BaseMvpActivity<V, P>(), ContractBase.BaseView {
    val TAG: String = "BaseActivity"
    protected lateinit var mPresenter: P

    companion object {
        val fragment = LoadingDialogFragment.instance
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter = getPresenter()
        mPresenter.declareVariable()
    }

    override fun showLoading() {
        fragment.show(supportFragmentManager, TAG)
    }

    override fun hideLoading() {
        if (null != fragment.dialog && fragment.dialog.isShowing)
            fragment.dismiss()
    }


    override fun showSuccess() {

    }

    override fun showFail(message: CharSequence) {
        toast(message)
    }
}