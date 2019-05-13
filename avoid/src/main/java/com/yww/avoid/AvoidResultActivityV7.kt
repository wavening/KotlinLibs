package com.yww.avoid

import android.content.Intent
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity

/**
 * @author  WAVENING
 */
class AvoidResultActivityV7 constructor(activity: AppCompatActivity) {
    private val tag = "AvoidResultActivityV7"
    private val avoidResultFragment: AvoidResultFragmentV4 = getAvoidResultFragmentV4(activity)

    fun startForResult(intent: Intent, requestCode: Int, callback: ActivityCallBack) {
        avoidResultFragment.startForResult(intent, requestCode, callback)
    }

    fun startForResult(clazz: Class<*>, requestCode: Int, callback: ActivityCallBack) {
        val intent = Intent(avoidResultFragment.activity, clazz)
        startForResult(intent, requestCode, callback)
    }


    private fun getAvoidResultFragmentV4(activity: AppCompatActivity): AvoidResultFragmentV4 {
        var fragment = findAvoidResultFragmentV4(activity)
        if (null == fragment) {
            fragment = AvoidResultFragmentV4()
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            fragmentManager.beginTransaction()
                .add(fragment, tag)
                .commitAllowingStateLoss()
            fragmentManager.executePendingTransactions()
        }
        return fragment
    }

    private fun findAvoidResultFragmentV4(activity: AppCompatActivity): AvoidResultFragmentV4? =
        activity.supportFragmentManager.findFragmentByTag(tag) as AvoidResultFragmentV4?

}