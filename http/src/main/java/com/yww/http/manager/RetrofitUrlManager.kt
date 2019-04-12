package com.yww.http.manager

import android.text.TextUtils
import android.util.Log
import com.yww.http.manager.other.OnUrlChangeListener
import com.yww.http.manager.other.UrlUtils
import com.yww.http.manager.parser.UrlParser
import com.yww.http.manager.parser.UrlParserDefault
import okhttp3.*
import java.io.IOException
import java.util.*

/**
 * @author  WAVENING
 */
class RetrofitUrlManager private constructor() {
    private val TAG = "RetrofitUrlManager"

    private val GLOBAL_DOMAIN_NAME = "me.jessyan.retrofiturlmanager.globalDomainName"
    private var baseUrl: HttpUrl? = null
    private var pathSize: Int = 0
    private var isRun = true //默认开始运行, 可以随时停止运行, 比如您在 App 启动后已经不需要再动态切换 BaseUrl 了
    private var debug = false//在 Debug  模式下可以打印日志
    private val mDomainNameHub = HashMap<String, HttpUrl>()
    private val mInterceptor: Interceptor
    private val mListeners = ArrayList<OnUrlChangeListener>()
    private lateinit var mUrlParser: UrlParser

    private object Holder {
        val INSTANCE = RetrofitUrlManager()
    }

    companion object {
        val instance = Holder.INSTANCE
        private val DOMAIN_NAME = "Domain-Name"
        val DOMAIN_NAME_HEADER = "$DOMAIN_NAME: "
        /**
         * 如果在 Url 地址中加入此标识符, 框架将不会对此 Url 进行任何切换 BaseUrl 的操作
         */
        val IDENTIFICATION_IGNORE = "#url_ignore"
        /**
         * 如果在 Url 地址中加入此标识符, 意味着您想对此 Url 开启超级模式, 框架会将 '=' 后面的数字作为 PathSize, 来确认最终需要被超级模式替换的 BaseUrl
         */
        val IDENTIFICATION_PATH_SIZE = "#baseurl_path_size="

        fun hasOkhttp3Dependence(): Boolean {
            return try {
                Class.forName("okhttp3.OkHttpClient")
                true
            } catch (e: ClassNotFoundException) {
                false
            }
        }
    }

    init {
        if (!hasOkhttp3Dependence()) { //使用本框架必须依赖 Okhttp
            throw IllegalStateException("Must be dependency Okhttp")
        }
        val urlParser = UrlParserDefault(this)
        setUrlParser(urlParser)
        this.mInterceptor = object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                // 可以在 App 运行时, 随时通过 setRun(false) 来结束本框架的运行
                return if (!isRun()) chain.proceed(chain.request())
                else chain.proceed(processRequest(chain.request()))
            }

        }
    }

    val dependence: Boolean = hasOkhttp3Dependence()


    /**
     * 将 [OkHttpClient.Builder] 传入, 配置一些本框架需要的参数
     *
     * @param builder [OkHttpClient.Builder]
     * @return [OkHttpClient.Builder]
     */
    fun with(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        UrlUtils.checkNotNull(builder, "builder cannot be null")
        return builder
            .addInterceptor(mInterceptor)
    }

    /**
     * 对 [Request] 进行一些必要的加工, 执行切换 BaseUrl 的相关逻辑
     *
     * @param request [Request]
     * @return [Request]
     */
    fun processRequest(request: Request): Request {
        val newBuilder = request.newBuilder()
        val url = request.url().toString()
        //如果 Url 地址中包含 IDENTIFICATION_IGNORE 标识符, 框架将不会对此 Url 进行任何切换 BaseUrl 的操作
        if (url.contains(IDENTIFICATION_IGNORE)) {
            return pruneIdentification(newBuilder, url)
        }
        val domainName: String? = obtainDomainNameFromHeaders(request)
        val httpUrl: HttpUrl?
        val listeners = listenersToArray()
        // 如果有 header,获取 header 中 domainName 所映射的 url,若没有,则检查全局的 BaseUrl,未找到则为null
        if (!TextUtils.isEmpty(domainName)) {
            notifyListener(request, domainName!!, listeners)
            httpUrl = fetchDomain(domainName)
            newBuilder.removeHeader(DOMAIN_NAME)
        } else {
            notifyListener(request, GLOBAL_DOMAIN_NAME, listeners)
            httpUrl = getGlobalDomain()
        }
        if (null != httpUrl) {
            val newUrl = mUrlParser.parseUrl(httpUrl, request.url())
            if (debug) {
                Log.d(
                    TAG,
                    "The new url is { " + newUrl.toString() + " }, old url is { " + request.url().toString() + " }"
                )
            }

            if (listeners != null) {
                for (i in listeners.indices) {
                    (listeners[i] as OnUrlChangeListener).onUrlChanged(
                        newUrl,
                        request.url()
                    ) // 通知监听器此 Url 的 BaseUrl 已被切换
                }
            }
            return newBuilder.url(newUrl).build()
        }
        return newBuilder.build()
    }

    /**
     * 将 `IDENTIFICATION_IGNORE` 从 Url 地址中修剪掉
     *
     * @param newBuilder [Request.Builder]
     * @param url        原始 Url 地址
     * @return 被修剪过 Url 地址的 [Request]
     */
    private fun pruneIdentification(newBuilder: Request.Builder, url: String): Request {
        val split = url.split(IDENTIFICATION_IGNORE.toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        val buffer = StringBuffer()
        for (s in split) {
            buffer.append(s)
        }
        return newBuilder
            .url(buffer.toString())
            .build()
    }

    /**
     * 通知所有监听器的 [onUrlChangeListener.onUrlChangeBefore] 方法
     *
     * @param request    [Request]
     * @param domainName 域名的别名
     * @param listeners  监听器列表
     */
    private fun notifyListener(request: Request, domainName: String, listeners: Array<Any>?) {
        if (listeners != null) {
            for (i in listeners.indices) {
                (listeners[i] as OnUrlChangeListener).onUrlChangeBefore(request.url(), domainName)
            }
        }
    }

    /**
     * 框架是否在运行
     *
     * @return `true` 为正在运行, `false` 为未运行
     */
    fun isRun(): Boolean {
        return this.isRun
    }

    /**
     * 控制框架是否运行, 在每个域名地址都已经确定, 不需要再动态更改时可设置为 `false`
     *
     * @param run `true` 为正在运行, `false` 为未运行
     */
    fun setRun(run: Boolean) {
        this.isRun = run
    }

    /**
     * 开启 Debug 模式下可以打印日志
     *
     * @param debug true 开启 Debug 模式
     */
    fun setDebug(debug: Boolean) {
        this.debug = debug
    }

    /**
     * 开启高级模式, 高级模式可以替换拥有多个 pathSegments 的 BaseUrl, 如: https://www.github.com/wiki/part
     * 高级模式的解析规则, 请看 [com.yunsign.lib_base.webrequest.retrofiturlmanager.parser.AdvancedUrlParser]
     * 注意, 如果没有开启高级模式, 默认为普通默认, 只能替换域名, 如: https://www.github.com
     *
     *
     * 注意, 遇到这个坑, 请别怪框架!!! Retrofit 的 BaseUrl 含有可被覆盖 pathSegment 的规则:
     * 举例: 您设置给 Retrofit 的 BaseUrl 是 "http://www.github.com/a/b/"
     * 然后您在接口方法上给的注解是 `@GET("/path")`, 这时 Retrofit 生成的最终路径是 "http://www.github.com/path"
     * "/a/b/" 被剪切掉了, 为什么? 因为您在 "path" 前面加上了 "/", "/" 会让 Retrofit 认为把您只想保留 BaseUrl 中的域名
     * 如果去掉 "/", `@GET("path")` 得到的最终路径才是 "http://www.github.com/a/b/path"
     *
     *
     * 所以如果在最终路径中, BaseUrl 的 "/a/b/" 因为您不熟悉规则而被剪切, 这时您应该在 [.startAdvancedModel]
     * 中传入被剪切的实际 BaseUrl "http://www.github.com", 而不是 http://www.github.com/a/b/, 否则框架会理解错误!
     *
     * @param baseUrl 您当时传入 Retrofit 的 BaseUrl
     * @see com.yunsign.lib_base.webrequest.retrofiturlmanager.parser.AdvancedUrlParser
     */
    fun startAdvancedModel(baseUrl: String) {
        UrlUtils.checkNotNull(baseUrl, "baseUrl cannot be null")
        startAdvancedModel(UrlUtils.checkUrl(baseUrl))
    }

    /**
     * 开启高级模式, 高级模式可以替换拥有多个 pathSegments 的 BaseUrl, 如: https://www.github.com/wiki/part
     * 高级模式的解析规则, 请看 [com.yunsign.lib_base.webrequest.retrofiturlmanager.parser.AdvancedUrlParser]
     * 注意, 如果没有开启高级模式, 默认为普通默认, 只能替换域名, 如: https://www.github.com
     *
     *
     * 注意, 遇到这个坑, 请别怪框架!!! Retrofit 的 BaseUrl 含有可被覆盖 pathSegment 的规则:
     * 举例: 您设置给 Retrofit 的 BaseUrl 是 "http://www.github.com/a/b/"
     * 然后您在接口方法上给的注解是 `@GET("/path")`, 这时 Retrofit 生成的最终路径是 "http://www.github.com/path"
     * "/a/b/" 被剪切掉了, 为什么? 因为您在 "path" 前面加上了 "/", "/" 会让 Retrofit 认为把您只想保留 BaseUrl 中的域名
     * 如果去掉 "/", `@GET("path")` 得到的最终路径才是 "http://www.github.com/a/b/path"
     *
     *
     * 所以如果在最终路径中, BaseUrl 的 "/a/b/" 因为您不熟悉规则而被剪切, 这时您应该在 [.startAdvancedModel]
     * 中传入被剪切的实际 BaseUrl "http://www.github.com", 而不是 http://www.github.com/a/b/, 否则框架会理解错误!
     *
     * @param baseUrl 您当时传入 Retrofit 的 BaseUrl
     * @see com.yunsign.lib_base.webrequest.retrofiturlmanager.parser.AdvancedUrlParser
     */
    @Synchronized
    fun startAdvancedModel(baseUrl: HttpUrl) {
        UrlUtils.checkNotNull(baseUrl, "baseUrl cannot be null")
        this.baseUrl = baseUrl
        this.pathSize = baseUrl.pathSize()
        val baseUrlpathSegments = baseUrl.pathSegments()
        if ("" == baseUrlpathSegments[baseUrlpathSegments.size - 1]) {
            this.pathSize -= 1
        }
    }

    /**
     * 获取 PathSegments 的总大小
     *
     * @return PathSegments 的 size
     */
    fun getPathSize(): Int {
        return pathSize
    }

    /**
     * 是否开启高级模式
     *
     * @return `true` 为开启, `false` 为未开启
     */
    fun isAdvancedModel(): Boolean {
        return baseUrl != null
    }

    /**
     * 获取 BaseUrl
     *
     * @return [.baseUrl]
     */
    fun getBaseUrl(): HttpUrl {
        return baseUrl!!
    }

    /**
     * 将 url 地址作为参数传入此方法, 并使用此方法返回的 Url 地址进行网络请求, 则会使此 Url 地址忽略掉本框架的所有更改效果
     *
     *
     * 使用场景:
     * 比如当您使用了 [.setGlobalDomain] 配置了全局 BaseUrl 后, 想请求一个与全局 BaseUrl
     * 不同的第三方服务商地址获取图片
     *
     * @param url Url 地址
     * @return 处理后的 Url 地址
     */
    fun setUrlNotChange(url: String): String {
        UrlUtils.checkNotNull(url, "url cannot be null")
        return url + IDENTIFICATION_IGNORE
    }

    /**
     * 将 url 地址和 pathSize 作为参数传入此方法, 并使用此方法返回的 Url 地址进行网络请求, 则会使此 Url 地址使用超级模式
     *
     *
     * 什么是超级模式? 请看 [RetrofitUrlManager] 上面的注释
     *
     * @param url      Url 地址
     * @param pathSize pathSize
     * @return 处理后的 Url 地址
     */
    fun setPathSizeOfUrl(url: String, pathSize: Int): String {
        UrlUtils.checkNotNull(url, "url cannot be null")
        if (pathSize < 0) {
            throw IllegalArgumentException("pathSize must be >= 0")
        }
        return url + IDENTIFICATION_PATH_SIZE + pathSize
    }

    /**
     * 全局动态替换 BaseUrl, 优先级: Header中配置的 BaseUrl > 全局配置的 BaseUrl
     * 除了作为备用的 BaseUrl, 当您项目中只有一个 BaseUrl, 但需要动态切换
     * 这种方式不用在每个接口方法上加入 Header, 就能实现动态切换 BaseUrl
     *
     * @param globalDomain 全局 BaseUrl
     */
    fun setGlobalDomain(globalDomain: String) {
        UrlUtils.checkNotNull(globalDomain, "globalDomain cannot be null")
        synchronized(mDomainNameHub) {
            mDomainNameHub.put(GLOBAL_DOMAIN_NAME, UrlUtils.checkUrl(globalDomain))
        }
    }

    /**
     * 获取全局 BaseUrl
     */
    @Synchronized
    fun getGlobalDomain(): HttpUrl? {
        return mDomainNameHub.get(GLOBAL_DOMAIN_NAME)
    }

    /**
     * 移除全局 BaseUrl
     */
    fun removeGlobalDomain() {
        synchronized(mDomainNameHub) {
            mDomainNameHub.remove(GLOBAL_DOMAIN_NAME)
        }
    }

    /**
     * 存放 Domain(BaseUrl) 的映射关系
     *
     * @param domainName
     * @param domainUrl
     */
    fun putDomain(domainName: String, domainUrl: String) {
        UrlUtils.checkNotNull(domainName, "domainName cannot be null")
        UrlUtils.checkNotNull(domainUrl, "domainUrl cannot be null")
        synchronized(mDomainNameHub) {
            mDomainNameHub.put(domainName, UrlUtils.checkUrl(domainUrl))
        }
    }

    /**
     * 取出对应 `domainName` 的 Url(BaseUrl)
     *
     * @param domainName
     * @return
     */
    @Synchronized
    fun fetchDomain(domainName: String?): HttpUrl? {
        UrlUtils.checkNotNull(domainName, "domainName cannot be null")
        return mDomainNameHub.get(domainName)
    }

    /**
     * 移除某个 `domainName`
     *
     * @param domainName `domainName`
     */
    fun removeDomain(domainName: String) {
        UrlUtils.checkNotNull(domainName, "domainName cannot be null")
        synchronized(mDomainNameHub) {
            mDomainNameHub.remove(domainName)
        }
    }

    /**
     * 清理所有 Domain(BaseUrl)
     */
    fun clearAllDomain() {
        mDomainNameHub.clear()
    }

    /**
     * 存放 Domain(BaseUrl) 的容器中是否存在这个 `domainName`
     *
     * @param domainName `domainName`
     * @return `true` 为存在, `false` 为不存在
     */
    @Synchronized
    fun haveDomain(domainName: String): Boolean {
        return mDomainNameHub.containsKey(domainName)
    }

    /**
     * 存放 Domain(BaseUrl) 的容器, 当前的大小
     *
     * @return 容量大小
     */
    @Synchronized
    fun domainSize(): Int {
        return mDomainNameHub.size
    }

    /**
     * 可自行实现 [UrlParser] 动态切换 Url 解析策略
     *
     * @param parser [UrlParser]
     */
    fun setUrlParser(parser: UrlParser) {
        UrlUtils.checkNotNull(parser, "parser cannot be null")
        this.mUrlParser = parser
    }

    /**
     * 注册监听器(当 Url 的 BaseUrl 被切换时会被回调的监听器)
     *
     * @param listener 监听器列表
     */
    fun registerUrlChangeListener(listener: OnUrlChangeListener) {
        UrlUtils.checkNotNull(listener, "listener cannot be null")
        synchronized(mListeners) {
            mListeners.add(listener)
        }
    }

    /**
     * 注销监听器(当 Url 的 BaseUrl 被切换时会被回调的监听器)
     *
     * @param listener 监听器列表
     */
    fun unregisterUrlChangeListener(listener: OnUrlChangeListener) {
        UrlUtils.checkNotNull(listener, "listener cannot be null")
        synchronized(mListeners) {
            mListeners.remove(listener)
        }
    }

    private fun listenersToArray(): Array<Any>? {
        var listeners: Array<Any>? = null
        synchronized(mListeners) {
            if (mListeners.size > 0) {
                listeners = mListeners.toTypedArray()
            }
        }
        return listeners
    }

    /**
     * 从 [Request.header] 中取出 DomainName
     *
     * @param request [Request]
     * @return DomainName
     */
    private fun obtainDomainNameFromHeaders(request: Request): String? {
        val headers = request.headers(DOMAIN_NAME)
        if (headers == null || headers.size == 0) {
            return null
        }
        if (headers.size > 1) {
            throw IllegalArgumentException("Only one Domain-Name in the headers")
        }
        return request.header(DOMAIN_NAME)
    }

}