package com.yww.rxjava2base.provider

import io.reactivex.Observer
import io.reactivex.disposables.Disposable


/**
 * @author  WAVENING
 */
abstract class BaseObserver<T, R : Response<T>> : Observer<R> {
    companion object {
        fun hasRetrofitDependence(): Boolean {
            return try {
                Class.forName("io.reactivex.Observer")
                true
            } catch (e: ClassNotFoundException) {
                false
            }
        }
    }

    init {
        when (hasRetrofitDependence()) {
            true -> {}
            else -> NullPointerException("not support")
        }
    }

    private var disposable: Disposable? = null
    override fun onSubscribe(d: Disposable) {
        this.disposable = d
    }

    override fun onNext(response: R) {
        when (response.code) {
            response.success -> success(response.result)
            else -> error(Throwable(response.error))
        }
    }

    override fun onComplete() {
        if (null != disposable) {
            disposable!!.dispose()
        }
    }

    override fun onError(e: Throwable) {
        if (null != disposable) {
            disposable!!.dispose()
        }
        error(e)
    }

    /**
     * 成功后的方法
     *
     * @param result
     */
    protected abstract fun success(result: T)

    /**
     * 失败后的方法
     *
     */
    protected abstract fun error(e: Throwable)
}