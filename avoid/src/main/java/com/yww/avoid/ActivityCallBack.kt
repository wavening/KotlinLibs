package com.yww.avoid

import android.content.Intent

/**
 * @author  WAVENING
 */
interface ActivityCallBack {
    /**
     * 界面返回时携带数据
     * @param requestCode
     * @param resultCode
     * @param data
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}