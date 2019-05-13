package com.yww.mvplib.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * @author  WAVENING
 */
open class BaseFragmentAdapter(fm: FragmentManager, private val list: List<Fragment>) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): Fragment = list[position]
}