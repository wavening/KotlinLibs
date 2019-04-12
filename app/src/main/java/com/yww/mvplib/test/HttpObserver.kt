package com.yww.mvplib.test

import com.yww.rxjava2base.provider.BaseObserver

/**
 * @author  WAVENING
 */
class HttpObserver<T> : BaseObserver<T, BaseResponse<T>>() {
    private lateinit var response: BaseResponse<T>
    override fun onNext(response: BaseResponse<T>) {
        this.response = response
        super.onNext(response)
    }

    override fun success(result: T) {

    }

    override fun error(e: Throwable) {
        when (e.message) {
            response.error -> ""
            else -> ""
        }
    }
}