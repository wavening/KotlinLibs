package com.yww.avoid

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.SparseArray

/**
 * @author  WAVENING
 */
class AvoidResultFragmentV4 : Fragment() {
    private val TAG = "AvoidResultFragmentV4"
    private val callBackArray = SparseArray<ActivityCallBack>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    fun startForResult(intent: Intent, requestCode: Int, callback: ActivityCallBack) {
        if (SingleIntentCheck.instance.checkSelfActivity(intent)) {
            callBackArray.put(requestCode, callback)
            startActivityForResult(intent, requestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //callback方式的处理
        callBackArray.get(requestCode)?.onActivityResult(requestCode, resultCode, data)
    }
}