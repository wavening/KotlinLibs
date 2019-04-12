package com.yww.utils.util

import android.util.Log
import com.yww.utils.BuildConfig


/**
 * @author  WAVENING
 */
class LogUtil private constructor() {
    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {
        var tag: String = "tag"

        fun log(content: String) {
            if (BuildConfig.DEBUG) {
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