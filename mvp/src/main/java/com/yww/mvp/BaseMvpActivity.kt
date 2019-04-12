package com.yww.mvp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.yww.mvp.core.*

/**
 * @author  WAVENING
 */
abstract class BaseMvpActivity<V : MvpView, P : MvpPresenter<V>> : AppCompatActivity(), DelegateCallback<V, P>,
    MvpView {
    private var activityDelegate: ActivityDelegate? = null
    private var presenter: P? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDelegate = ActivityDelegateImpl(this)
        activityDelegate!!.onCreate()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        activityDelegate!!.onNewIntent()
    }

    override fun onRestart() {
        super.onRestart()
        activityDelegate!!.onRestart()
    }

    override fun onStart() {
        super.onStart()
        activityDelegate!!.onStart()
    }

    override fun onResume() {
        super.onResume()
        activityDelegate!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        activityDelegate!!.onPause()
    }

    override fun onStop() {
        super.onStop()
        activityDelegate!!.onStop()
    }


    override fun onDestroy() {
        super.onDestroy()
        activityDelegate!!.onDestroy(false)
    }

    /**
     * 暴露一个创建的方法用于创建presenter
     *
     * @return P
     */
    protected abstract fun reCreatePresenter(): P

    /**
     * 这个方法由MvpInternalDelegate 调用 BaseDelegateCallback 来创建Presenter
     */
    override fun createPresenter(): P {
        presenter = reCreatePresenter()
        return presenter as P
    }

    override fun getPresenter(): P {
        if (null == presenter) {
            presenter = createPresenter()
        }
        return presenter as P
    }

    override fun setNewPresenter(presenter: P) {
        this.presenter = presenter
    }

    @Suppress("UNCHECKED_CAST")
    override fun getMvpView(): V = this as V

}