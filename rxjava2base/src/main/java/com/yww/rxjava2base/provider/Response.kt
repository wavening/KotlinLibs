package com.yww.rxjava2base.provider

/**
 * @author  WAVENING
 */
interface Response<T> {
    val success: Int
    val error: String
    var code: Int
    var message: String
    var result: T

}