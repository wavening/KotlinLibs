package com.yww.utils.util

import android.annotation.SuppressLint
import android.app.Application
import java.lang.reflect.InvocationTargetException

/**
 * @author  WAVENING
 */
class Util {
    private object Holder {
        val INSTANCE = Util()
    }

    companion object {
        private val instance: Util = Holder.INSTANCE
        private var application: Application? = null
        fun init(app: Application) {
            application = app
        }

        fun getApplication(): Application? {
            if (null == application) {
                val app = instance.getAppByReflect()
                if (null != app) {
                    application = app
                }
            }
            return application
        }

//        fun getCurrentActivity():Activity{
//
//        }
    }


    private fun getAppByReflect(): Application? {
        try {
            @SuppressLint("PrivateApi")
            val activityThread = Class.forName("android.app.ActivityThread")
            val thread = activityThread.getMethod("currentActivityThread").invoke(null)
            val app = activityThread.getMethod("getApplication").invoke(thread)
                ?: throw NullPointerException("u should init first")
            return app as Application
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

        throw NullPointerException("u should init first")
    }

}