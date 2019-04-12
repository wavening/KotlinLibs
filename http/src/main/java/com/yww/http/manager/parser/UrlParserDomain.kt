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
class UrlParserDomain(var manager: RetrofitUrlManager) : UrlParser {
    private val cache: Cache<String, String> = LruCache(100)
    override fun parseUrl(domainUrl: HttpUrl?, url: HttpUrl): HttpUrl {
        // 如果 HttpUrl.parse(url); 解析为 null 说明,url 格式不正确,正确的格式为 "https://github.com:443"
        // http 默认端口 80, https 默认端口 443, 如果端口号是默认端口号就可以将 ":443" 去掉
        // 只支持 http 和 https
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
            newPathSegments.addAll(url.encodedPathSegments())

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
        return domainUrl.encodedPath() + url.encodedPath()
    }

}