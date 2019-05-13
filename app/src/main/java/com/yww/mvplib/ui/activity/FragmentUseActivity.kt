package com.yww.mvplib.ui.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.SparseArray
import com.yww.mvplib.R
import com.yww.mvplib.ui.fragment.FragmentUseChildFragment
import kotlinx.android.synthetic.main.activity_fragment_use.*

/**
 * @author  WAVENING
 */
class FragmentUseActivity : AppCompatActivity() {
    private val CHILD_FRAGMENT_MANAGER_USE = 0
//    private val TAB_FRAGMENT_MANAGER_USE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_use)
        val fragmentArray: SparseArray<Fragment> = SparseArray()
        fragmentArray.put(CHILD_FRAGMENT_MANAGER_USE, FragmentUseChildFragment())
//        fragmentArray.put(TAB_FRAGMENT_MANAGER_USE, FragmentUseChildFragment())
        supportFragmentManager.beginTransaction()
            .add(R.id.app_fragment_use_frame_layout, fragmentArray.get(CHILD_FRAGMENT_MANAGER_USE))
            .show(fragmentArray.get(CHILD_FRAGMENT_MANAGER_USE)).commit()
        app_fragment_use_radio_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.app_fragment_use_radio_button_first -> {
                    supportFragmentManager.beginTransaction()
                        .show(fragmentArray.get(CHILD_FRAGMENT_MANAGER_USE))
                        .commit()
                }
//                else -> {
//                    supportFragmentManager.beginTransaction()
//                        .show(fragmentArray.get(TAB_FRAGMENT_MANAGER_USE))
//                        .commit()
//                }
            }

        }
    }
}