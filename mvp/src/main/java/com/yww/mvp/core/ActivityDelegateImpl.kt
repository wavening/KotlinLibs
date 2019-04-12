package com.yww.mvp.core

/**
 * @author  WAVENING
 */
internal class ActivityDelegateImpl<V : MvpView, P : MvpPresenter<V>>(callback: DelegateCallback<V, P>) :
    ActivityDelegate {
    private var internalDelegate: MvpInternalDelegate<V, P> = MvpInternalDelegate(callback)

    override fun onCreate() {
        internalDelegate.createPresenter()
        internalDelegate.attachView()
    }

    override fun onNewIntent() {
    }

    override fun onRestart() {
    }

    override fun onStart() {
    }

    override fun onResume() {
    }

    override fun onPause() {
    }

    override fun onStop() {
    }

    override fun onDestroy(remainInstance: Boolean) {
        internalDelegate.detachView(remainInstance)
    }
}