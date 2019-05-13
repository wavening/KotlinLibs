package com.yww.utils.stragedy.brand

import com.yww.utils.extension.openPage
import com.yww.utils.impl.IBrand
import com.yww.utils.manager.BrandManager
import com.yww.utils.stragedy.brand.phones.Sysconf

/**
 * @Author  WAVENING
 * @Date    2019/4/25-16:26
 */
internal class BrandImpl : IBrand {


    private object Holder {
        val INSTANCE = BrandImpl()
    }

    companion object {
        val instance: BrandImpl = Holder.INSTANCE
    }

    override fun openPhoneManager() {
        try {
            openPage(BrandManager.instance.brandManager.managerIntent)
        } catch (e: Exception) {
            openPage(Sysconf().managerIntent)
        }

    }


}