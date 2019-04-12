package com.yww.utils.manager

import android.app.Activity
import android.support.annotation.Keep
import java.util.*

/**
 * @author  WAVENING
 */
@Keep
class ActivityManager {
    private object Holder {
       internal val INSTANCE = ActivityManager()
    }

    companion object {
        @Keep
        val instance:ActivityManager = Holder.INSTANCE
    }

    private val activityStack: Stack<Activity> = Stack()

    /**
     * 添加Activity到堆栈
     */
    fun addActviity(activity: Activity) = activityStack.add(activity)

    /**
     * 获取栈顶Activity（堆栈中最后一个压入的）
     */
    fun findLastActivity(): Activity? = activityStack.lastElement()

    /**
     * 获取栈顶Activity（堆栈中最后一个压入的）
     */
    fun findActivityByClassName(clazz: Class<*>): Activity? {
        var act: Activity? = null
        for (activity in activityStack) {
            when (clazz == activity::class.java) {
                true -> act = activity
                else -> act = null
            }
        }
        return act
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity) {
        if (activityStack.contains(activity) && !activity.isFinishing) {
            activityStack.remove(activity)
            activity.finish()
        }
    }

    /**
     * 结束所有Activity
     */
    private fun finishAllActivity() {
        for (activity in activityStack) {
            if (!activity.isFinishing) activity.finish()
        }
        activityStack.clear()
    }

    /**
     * 退出应用程序
     * 退出进程
     */
    fun exitAppProcess() {
        try {
            finishAllActivity()
            System.exit(0)
            android.os.Process.killProcess(android.os.Process.myPid())
        } catch (e: Exception) {
        }

    }
}