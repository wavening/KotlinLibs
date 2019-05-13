package com.yww.mvplib.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.RadioButton
import com.yww.mvplib.R
import com.yww.mvplib.base.BaseFragment
import com.yww.mvplib.ui.adapter.BaseFragmentAdapter
import com.yww.mvplib.ui.listener.BasePagerChangeListener
import kotlinx.android.synthetic.main.fragment_child_fragment_use.*

/**
 * @author  WAVENING
 */
class FragmentUseChildFragment : BaseFragment() {
    override var layoutId: Int = R.layout.fragment_child_fragment_use
    private val fragmentList: MutableList<Fragment> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentList.add(ChildUseFragment("第一"))
        fragmentList.add(ChildUseFragment("第二"))
        fragmentList.add(ChildUseFragment("第三"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        app_fragment_use_child_viewpager.removeOnPageChangeListener(pagerChangeListener)
    }

    override fun onLazyLoaded() {
        app_fragment_use_child_viewpager.adapter = BaseFragmentAdapter(childFragmentManager, fragmentList)
        app_fragment_use_child_viewpager.addOnPageChangeListener(pagerChangeListener)
        app_fragment_use_child_viewpager.currentItem = 0
        app_fragment_child_use_radio_group.setOnCheckedChangeListener { _, checkedId ->
            selectPage@ for (i in 0 until app_fragment_child_use_radio_group.childCount) when (checkedId) {
                app_fragment_child_use_radio_group.getChildAt(i).id -> {
                    app_fragment_use_child_viewpager.currentItem = i
                    break@selectPage
                }
            }
        }
    }

    private val pagerChangeListener: BasePagerChangeListener = object : BasePagerChangeListener() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            val radioBtn = app_fragment_child_use_radio_group.getChildAt(position) as RadioButton
            radioBtn.isChecked = true
        }
    }

}