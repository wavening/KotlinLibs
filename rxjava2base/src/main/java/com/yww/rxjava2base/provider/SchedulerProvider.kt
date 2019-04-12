package com.yww.rxjava2base.provider

import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 *  RxJava2 线程调度常用工具类
 * @author  WAVENING
 */
class SchedulerProvider private constructor() : BaseScheduler {

    private object Holder {
        val INSTANCE = SchedulerProvider()
    }

    companion object {
        val instance = Holder.INSTANCE
    }

    override fun computation(): Scheduler {
        return Schedulers.computation()
    }

    override fun io(): Scheduler {
        return Schedulers.io()
    }

    override fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    fun <T> applyScheduler(): ObservableTransformer<T, T> =
        ObservableTransformer {
            it.subscribeOn(io())
                .unsubscribeOn(io())
                .subscribeOn(ui())
                .observeOn(ui())
        }

}