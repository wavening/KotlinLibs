package com.yww.mvp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yww.mvp.core.*

/**
 * @author  WAVENING
 */
 abstract class BaseMvpFragment<V : MvpView, P : MvpPresenter<V>> : Fragment(), DelegateCallback<V, P>, MvpView {
    private var fragmentDelegate: FragmentDelegate? = null
    private var presenter: P? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentDelegate = FragmentDelegateImpl(this)
        fragmentDelegate!!.onCreateView()
        return createFragmentView()
    }
    /**
     * 创建Fragment的视图
     *
     * @return View
     */
    abstract fun createFragmentView(): View?

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
    override fun getMvpView(): V =  this as V

}