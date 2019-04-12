package com.yww.http.manager.parser

import android.text.TextUtils
import com.yww.http.manager.RetrofitUrlManager
import com.yww.http.manager.cache.Cache
import com.yww.http.manager.cache.LruCache
import okhttp3.HttpUrl
import java.util.*

/**
 * @author  WAVENING
 */
class UrlParserAdvanced(var manager: RetrofitUrlManager) : UrlParser {
    private val cache: Cache<String, String> = LruCache(100)


    override fun parseUrl(domainUrl: HttpUrl?, url: HttpUrl): HttpUrl {
        if (null == domainUrl) {
            return url
        }
        val builder = url.newBuilder()

        if (TextUtils.isEmpty(cache.get(getKey(domainUrl, url)))) {
            for (i in 0 until url.pathSize()) {
                //当删除了上一个 index, PathSegment 的 item 会自动前进一位, 所以 remove(0) 就好
                builder.removePathSegment(0)
            }

            val newPathSegments = ArrayList<String>()
            newPathSegments.addAll(domainUrl.encodedPathSegments())

            if (url.pathSize() > manager.getPathSize()) {
                val encodedPathSegments = url.encodedPathSegments()
                for (i in manager.getPathSize() until encodedPathSegments.size) {
                    newPathSegments.add(encodedPathSegments.get(i))
                }
            } else if (url.pathSize() < manager.getPathSize()) {
                throw IllegalArgumentException(
                    String.format(
                        "Your final path is %s, but the baseUrl of your RetrofitUrlManager#startAdvancedModel is %s",
                        url.scheme() + "://" + url.host() + url.encodedPath(),
                        manager.getBaseUrl().scheme() + "://"
                                + manager.getBaseUrl().host()
                                + manager.getBaseUrl().encodedPath()
                    )
                )
            }

            for (PathSegment in newPathSegments) {
                builder.addEncodedPathSegment(PathSegment)
            }
        } else {
            builder.encodedPath(cache.get(getKey(domainUrl, url)))
        }

        val httpUrl = builder
            .scheme(domainUrl.scheme())
            .host(domainUrl.host())
            .port(domainUrl.port())
            .build()

        if (TextUtils.isEmpty(cache.get(getKey(domainUrl, url)))) {
            cache.put(getKey(domainUrl, url), httpUrl.encodedPath())
        }
        return httpUrl
    }

    private fun getKey(domainUrl: HttpUrl, url: HttpUrl): String {
        return (domainUrl.encodedPath() + url.encodedPath()
                + manager.getPathSize())
    }
}