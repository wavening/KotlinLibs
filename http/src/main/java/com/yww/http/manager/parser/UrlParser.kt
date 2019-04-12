package com.yww.http.manager.parser

import com.yww.http.manager.RetrofitUrlManager
import okhttp3.HttpUrl
import okhttp3.Request

/**
 * @author  WAVENING
 */
interface UrlParser {
    /**
     * 将 [RetrofitUrlManager.mDomainNameHub] 中映射的 URL 解析成完整的[HttpUrl]
     * 用来替换 @[Request.url] 达到动态切换 URL
     *
     * @param domainUrl 用于替换的 URL 地址
     * @param url      旧 URL 地址
     * @return
     */
    fun parseUrl(domainUrl: HttpUrl?, url: HttpUrl): HttpUrl
}