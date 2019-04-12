package com.yww.http.manager.other

import okhttp3.HttpUrl

/**
 * @author  WAVENING
 */
/**
 * ================================================
 * Url 监听器
 * <p>
 * Created by JessYan on 20/07/2017 14:18
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
interface OnUrlChangeListener {
    /**
     * 此方法在框架使用 `domainName` 作为 key,从 [RetrofitUrlManager.mDomainNameHub]
     * 中取出对应的 BaseUrl 构建新的 Url 之前会被调用
     *
     *
     * 可以使用此回调确保 [RetrofitUrlManager.mDomainNameHub] 中是否已经存在自己期望的 BaseUrl
     * 如果不存在可以在此方法中进行 [RetrofitUrlManager.putDomain]
     *
     * @param oldUrl
     * @param domainName
     */
    fun onUrlChangeBefore(oldUrl: HttpUrl, domainName: String)

    /**
     * 当 Url 的 BaseUrl 被切换时回调
     * 调用时间是在接口请求服务器之前
     *
     * @param newUrl
     * @param oldUrl
     */
    fun onUrlChanged(newUrl: HttpUrl, oldUrl: HttpUrl)
}