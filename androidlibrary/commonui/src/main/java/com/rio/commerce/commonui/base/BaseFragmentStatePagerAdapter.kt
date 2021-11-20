package com.rio.commerce.commonui.base

import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class BaseFragmentStatePagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String?>()

    fun addFragment(fragment: Fragment, title: String?) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    fun getPageTitle(position: Int): CharSequence? {
        return if (!TextUtils.isEmpty(mFragmentTitleList[position])) {
            mFragmentTitleList[position]
        } else {
            null
        }
    }


    override fun getItemCount(): Int {
        return mFragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }

}