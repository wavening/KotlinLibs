package com.yww.avoid

import android.app.Activity
import android.app.FragmentManager
import android.content.Intent

/**
 * @author  WAVENING
 */
class AvoidResultActivity constructor(activity: Activity)  {
    private val TAG = "AvoidResultActivity"
    private val avoidResultFragment: AvoidResultFragment

    init {
        avoidResultFragment = getAvoidOnResultFragment(activity)
    }

     fun startForResult(intent: Intent, requestCode: Int, callback: ActivityCallBack) {
        avoidResultFragment.startForResult(intent, requestCode, callback)
    }

     fun startForResult(clazz: Class<*>, requestCode: Int, callback: ActivityCallBack) {
        val intent = Intent(avoidResultFragment.activity, clazz)
        startForResult(intent, requestCode, callback)
    }

    private fun getAvoidOnResultFragment(activity: Activity): AvoidResultFragment {
        var fragment = findAvoidOnResultFragment(activity)
        if (null == fragment) {
            fragment = AvoidResultFragment()
            val fragmentManager: FragmentManager = activity.fragmentManager
            fragmentManager.beginTransaction()
                .add(fragment, TAG)
                .commitAllowingStateLoss()
            fragmentManager.executePendingTransactions()
        }
        return fragment
    }

    private fun findAvoidOnResultFragment(activity: Activity): AvoidResultFragment? =
        activity.fragmentManager.findFragmentByTag(TAG) as AvoidResultFragment


}