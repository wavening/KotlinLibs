package com.yww.http.manager.parser

import android.text.TextUtils
import com.yww.http.manager.RetrofitUrlManager
import com.yww.http.manager.RetrofitUrlManager.Companion.IDENTIFICATION_PATH_SIZE
import com.yww.http.manager.cache.Cache
import com.yww.http.manager.cache.LruCache
import okhttp3.HttpUrl
import java.util.*

/**
 * @author  WAVENING
 */
class UrlParserSuper(var manager: RetrofitUrlManager) : UrlParser {
    private val cache: Cache<String, String> = LruCache(100)
    override fun parseUrl(domainUrl: HttpUrl?, url: HttpUrl): HttpUrl {
        if (null == domainUrl) {
            return url
        }
        val builder = url.newBuilder()
        val pathSize = resolvePathSize(url, builder)
        if (TextUtils.isEmpty(cache.get(getKey(domainUrl, url, pathSize)))) {
            for (i in 0 until url.pathSize()) {
                //当删除了上一个 index, PathSegment 的 item 会自动前进一位, 所以 remove(0) 就好
                builder.removePathSegment(0)
            }
            val newPathSegments = ArrayList<String>()
            newPathSegments.addAll(domainUrl.encodedPathSegments())


            if (url.pathSize() > pathSize) {
                val encodedPathSegments = url.encodedPathSegments()
                for (i in pathSize until encodedPathSegments.size) {
                    newPathSegments.add(encodedPathSegments.get(i))
                }
            } else if (url.pathSize() < pathSize) {
                throw IllegalArgumentException(
                    String.format(
                        "Your final path is %s, the pathSize = %d, but the #baseurl_path_size = %d, #baseurl_path_size must be less than or equal to pathSize of the final path",
                        url.scheme() + "://" + url.host() + url.encodedPath(), url.pathSize(), pathSize
                    )
                )
            }

            for (PathSegment in newPathSegments) {
                builder.addEncodedPathSegment(PathSegment)
            }
        } else {
            builder.encodedPath(cache.get(getKey(domainUrl, url, pathSize)))
        }

        val httpUrl = builder
            .scheme(domainUrl.scheme())
            .host(domainUrl.host())
            .port(domainUrl.port())
            .build()

        if (TextUtils.isEmpty(cache.get(getKey(domainUrl, url, pathSize)))) {
            cache.put(getKey(domainUrl, url, pathSize), httpUrl.encodedPath())
        }
        return httpUrl
    }

    private fun getKey(domainUrl: HttpUrl, url: HttpUrl, PathSize: Int): String {
        return (domainUrl.encodedPath() + url.encodedPath()
                + PathSize)
    }

    private fun resolvePathSize(httpUrl: HttpUrl, builder: HttpUrl.Builder): Int {
        val fragment = httpUrl.fragment()

        var pathSize = 0
        val newFragment = StringBuffer()

        if (fragment!!.indexOf("#") == -1) {
            val split = fragment.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (split.size > 1) {
                pathSize = Integer.parseInt(split[1])
            }
        } else {
            if (fragment.indexOf(IDENTIFICATION_PATH_SIZE) == -1) {
                val index = fragment.indexOf("#")
                newFragment.append(fragment.substring(index + 1, fragment.length))
                val split =
                    fragment.substring(0, index).split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (split.size > 1) {
                    pathSize = Integer.parseInt(split[1])
                }
            } else {
                val split =
                    fragment.split(IDENTIFICATION_PATH_SIZE.toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                newFragment.append(split[0])
                if (split.size > 1) {
                    val index = split[1].indexOf("#")
                    if (index != -1) {
                        newFragment.append(split[1].substring(index, split[1].length))
                        val substring = split[1].substring(0, index)
                        if (!TextUtils.isEmpty(substring)) {
                            pathSize = Integer.parseInt(substring)
                        }
                    } else {
                        pathSize = Integer.parseInt(split[1])
                    }
                }
            }
        }
        if (TextUtils.isEmpty(newFragment.toString())) {
            builder.fragment(null)
        } else {
            builder.fragment(newFragment.toString())
        }
        return pathSize
    }
}