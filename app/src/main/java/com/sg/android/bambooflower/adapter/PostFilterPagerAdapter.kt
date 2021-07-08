package com.sg.android.bambooflower.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class PostFilterPagerAdapter(
    private var list: List<Fragment> = listOf(),
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return list[position].hashCode().toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return list.any { it.hashCode().toLong() == itemId }
    }

    fun syncData(_list: List<Fragment>) {
        list = _list
        notifyDataSetChanged()
    }
}