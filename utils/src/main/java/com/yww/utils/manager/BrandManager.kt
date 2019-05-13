package com.yww.utils.manager

import android.os.Build
import android.support.annotation.Keep
import com.yww.utils.impl.IBrand
import com.yww.utils.stragedy.brand.phones.*

/**
 * @Author  WAVENING
 * @Date    2019/4/25-16:22
 */
@Keep
class BrandManager {
    /**
     * Build.MANUFACTURER
     */
    private val MANUFACTURER_HUAWEI = "Huawei"//华为
    private val MANUFACTURER_MEIZU = "Meizu"//魅族
    private val MANUFACTURER_XIAOMI = "Xiaomi"//小米
    private val MANUFACTURER_SONY = "Sony"//索尼
    private val MANUFACTURER_OPPO = "OPPO"
    private val MANUFACTURER_LG = "Lg"
    private val MANUFACTURER_VIVO = "vivo"
    private val MANUFACTURER_SAMSUNG = "samsung"//三星
    private val MANUFACTURER_LETV = "Letv"//乐视
    private val MANUFACTURER_ZTE = "ZTE"//中兴
    private val MANUFACTURER_YULONG = "YuLong"//酷派
    private val MANUFACTURER_LENOVO = "LENOVO"//联想
    private val MANUFACTURER_COOLPAD = "Coolpad"//酷派

    private object Holder {
        val INSTANCE = BrandManager()
    }

    companion object {
        val instance: BrandManager = Holder.INSTANCE
    }


    val brandManager: IBrand.IManager = when (Build.MANUFACTURER.toUpperCase()) {
        MANUFACTURER_HUAWEI.toUpperCase() -> Huawei()
        MANUFACTURER_LETV.toUpperCase() -> Letv()
        MANUFACTURER_LG.toUpperCase() -> Lg()
        MANUFACTURER_MEIZU.toUpperCase() -> Meizu()
        MANUFACTURER_OPPO.toUpperCase() -> Oppo()
        MANUFACTURER_VIVO.toUpperCase() -> Vivo()
        MANUFACTURER_SONY.toUpperCase() -> Sony()
        MANUFACTURER_SAMSUNG.toUpperCase() -> Samsung()
        MANUFACTURER_COOLPAD.toUpperCase() -> Coolpad()
        MANUFACTURER_XIAOMI.toUpperCase() -> Xiaomi()
        else -> Appinfo()
    }
}