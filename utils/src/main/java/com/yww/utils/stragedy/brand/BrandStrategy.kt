package com.yww.utils.stragedy.brand

import android.support.annotation.Keep
import com.yww.utils.impl.IBrand
import java.lang.ref.WeakReference

/**
 * @Author  WAVENING
 * @Date    2019/4/25-16:27
 */
@Keep
class BrandStrategy private constructor() {
    private var reference: WeakReference<IBrand> = WeakReference(BrandImpl.instance)

    private object Holder {
        val INSTANCE = BrandStrategy()
    }

    companion object {
        @Keep
        @JvmStatic
        val instance: BrandStrategy = Holder.INSTANCE


        @Keep
        @JvmStatic
        fun init(strategy: IBrand) {
            if (strategy != this.instance.reference.get())
                this.instance.reference = WeakReference(strategy)
        }
    }

    fun openPageInPermissionManager() {
        reference.get()?.openPhoneManager()
    }


}