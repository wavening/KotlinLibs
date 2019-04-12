package com.yww.mvplib.test

import com.yww.rxjava2base.provider.Response

/**
 * @author  WAVENING
 */
class BaseResponse<T> : Response<T> {
    override val success: Int
        get() = 0
    override val error: String
        get() = "error"
    override var code: Int
        get() = code
        set(value) {
            code = value
        }
    override var message: String
        get() = message
        set(value) {
            message = value
        }
    override var result: T
        get() = result
        set(value) {
            result = value
        }
}