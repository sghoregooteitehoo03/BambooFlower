package com.sg.android.bambooflower.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ShopPagerAdapter(
    private var fragList: List<Fragment> = listOf(),
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int =
        fragList.size

    override fun createFragment(position: Int): Fragment {
        return fragList[position]
    }

    override fun getItemId(position: Int): Long {
        return fragList[position].hashCode().toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return fragList.any { it.hashCode().toLong() == itemId }
    }
}