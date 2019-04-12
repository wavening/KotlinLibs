package com.yww.mvp.core

/**
 * @author  WAVENING
 */
internal class FragmentDelegateImpl<V : MvpView, P : MvpPresenter<V>>(callback: DelegateCallback<V, P>) :
    FragmentDelegate {
    private val internalDelegate:MvpInternalDelegate<V,P> = MvpInternalDelegate(callback)

    override fun onCreateView() {
        internalDelegate.createPresenter()
        internalDelegate.attachView()
    }
    override fun onActivityCreated() {
    }

    override fun onResume() {
    }

    override fun onPause() {
    }

    override fun onUserVisibleHint(isVisible: Boolean) {
    }

    override fun onStop() {
    }

    override fun onDestroyView() {
        internalDelegate.detachView(false)
    }


}