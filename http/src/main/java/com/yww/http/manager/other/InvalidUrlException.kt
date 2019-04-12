package com.yww.http.manager.other

import android.text.TextUtils

/**
 * @author  WAVENING
 */
/**
 * ================================================
 * Url 无效的异常
 * <p>
 * Created by JessYan on 2017/7/24.
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
class InvalidUrlException constructor(url: String) : RuntimeException() {
    init {
        ("You've configured an invalid url : " + if (TextUtils.isEmpty(url)) "EMPTY_OR_NULL_URL" else url)
    }
}