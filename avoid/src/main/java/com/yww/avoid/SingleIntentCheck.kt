package com.yww.avoid

import android.content.Intent
import android.os.SystemClock

/**
 * @author  WAVENING
 */
class SingleIntentCheck {
    private var internal_time = 0L
    private var click_tag = ""


    private object Holder {
        val INSTANCE = SingleIntentCheck()
    }

    companion object {
        val instance = Holder.INSTANCE
        var click_interval_time = 500L
    }

    fun checkSelfActivity(intent: Intent): Boolean {
        //默认检查通过
        val result = true
        //标记对象
        val tag: String?
        if (null != intent.component) {
            //显示跳转
            tag = intent.component!!.className
        } else if (null != intent.action) {
            //隐示跳转
            tag = intent.action
        } else {
            return result
        }
        if (tag == click_tag && internal_time >= SystemClock.uptimeMillis() - click_interval_time) {
            //检查不通过
            return false
        }
        //记录启动标记和时间
        click_tag = tag
        internal_time = SystemClock.uptimeMillis()
        return result
    }

}