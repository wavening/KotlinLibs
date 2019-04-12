package com.yww.http.manager.parser

import com.yww.http.manager.RetrofitUrlManager
import com.yww.http.manager.RetrofitUrlManager.Companion.IDENTIFICATION_PATH_SIZE
import okhttp3.HttpUrl

/**
 * @author  WAVENING
 */
class UrlParserDefault(var manager: RetrofitUrlManager) : UrlParser {

    @Volatile
    private lateinit var domainUrlParser: UrlParser
    @Volatile
    private lateinit var advancedUrlParser: UrlParser
    @Volatile
    private lateinit var superUrlParser: UrlParser

    override fun parseUrl(domainUrl: HttpUrl?, url: HttpUrl): HttpUrl {
        //如果是超级模式则使用超级解析器
        if (url.toString().contains(IDENTIFICATION_PATH_SIZE)) {
            return getSuperParser().parseUrl(domainUrl, url)
        }
        //如果是高级模式则使用高级解析器
        if (manager.isAdvancedModel()) {
            return getAdvancedParser().parseUrl(domainUrl, url)
        }
        return domainUrlParser.parseUrl(domainUrl, url)
    }

    private fun getSuperParser(): UrlParser {
        if (null == superUrlParser) {
            synchronized(this@UrlParserDefault) {
                if (null == superUrlParser) {
                    superUrlParser = UrlParserSuper(manager)
                }
            }
        }
        return superUrlParser
    }

    private fun getAdvancedParser(): UrlParser {
        if (null == advancedUrlParser) {
            synchronized(this@UrlParserDefault) {
                if (null == advancedUrlParser) {
                    advancedUrlParser = UrlParserAdvanced(manager)
                }
            }
        }
        return advancedUrlParser
    }
}