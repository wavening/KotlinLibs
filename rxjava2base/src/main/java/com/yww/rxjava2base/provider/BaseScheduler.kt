package com.yww.rxjava2base.provider

import io.reactivex.Scheduler
import io.reactivex.annotations.NonNull

/**
 * @author  WAVENING
 */
interface BaseScheduler {
    @NonNull
    fun computation(): Scheduler

    @NonNull
    fun io(): Scheduler

    @NonNull
    fun ui(): Scheduler
}