package com.yww.avoid

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.util.SparseArray

/**
 * @author  WAVENING
 */
class AvoidResultFragment : Fragment() {

    private val TAG = "AvoidResultFragment"
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        //callback方式的处理
        val callBack = callBackArray.get(requestCode)
        callBack?.onActivityResult(requestCode, resultCode, data)
    }
}