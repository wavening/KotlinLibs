package com.yww.http.manager.other

import okhttp3.HttpUrl

/**
 * @author  WAVENING
 */
object UrlUtils {

    fun checkUrl(url: String): HttpUrl {
        val parseUrl = HttpUrl.parse(url)
        return parseUrl ?: throw InvalidUrlException(url)
    }

    fun <T> checkNotNull(`object`: T?, message: String): T {
        if (`object` == null) {
            throw NullPointerException(message)
        }
        return `object`
    }
}