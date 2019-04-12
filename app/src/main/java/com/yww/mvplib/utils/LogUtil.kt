package com.yww.mvplib.utils

import android.util.Log
import com.yww.mvplib.BuildConfig


/**
 * @author  WAVENING
 */
class LogUtil private constructor() {
    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {
        private const val debug: Boolean = true
        private var tag: String = "tag"

        fun log(content: String) {
            if (BuildConfig.DEBUG || debug) {
                Log.e(tag, "[" + lineMethod() + "]\n" + content)
            }
        }

        private fun lineMethod(): String {
            val traceElement: StackTraceElement = Exception().stackTrace[2]
            return StringBuffer("[")
                .append(traceElement.fileName)
                .append("|" + traceElement.className)
                .append("|" + traceElement.lineNumber)
                .append("|" + traceElement.methodName)
                .append("]")
                .toString()
        }

    }
}