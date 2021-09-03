package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.ShopPagerAdapter
import com.sg.android.bambooflower.databinding.FragmentShopBinding
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.shopFrag.ShopViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShopFragment : Fragment(), View.OnClickListener {
    private val mViewModel by viewModels<ShopViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val fragList: List<Fragment> = listOf<Fragment>(
        ShopListFragment(0),
        ShopListFragment(1)
    )
    private val pagerCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            mViewModel.pagerPos.value = position
        }
    }

    private lateinit var shopPagerView: ViewPager2
    private lateinit var shopPagerAdapter: ShopPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentShopBinding.inflate(inflater)
        shopPagerAdapter = ShopPagerAdapter(
            fragList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.gviewmodel = gViewModel
            this.clickListener = this@ShopFragment

            with(shopPager) {
                shopPagerView = this
                adapter = shopPagerAdapter
                registerOnPageChangeCallback(pagerCallback)
            }

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)


    }

    override fun onStart() {
        super.onStart()
        // 툴바 설정
        with((activity as MainActivity)) {
            supportActionBar?.title = "상점"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            showToolbar()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> false
        }
    }

    // 버튼 액션
    override fun onClick(v: View) {
        when (v.id) {
            R.id.filter_flower_text -> { // "꽃" 텍스트
                val pos = mViewModel.pagerPos.value!!
                if (pos != 0) { // 두번 동작 안되게
                    shopPagerView.setCurrentItem(0, true)
                }
            }
            R.id.filter_item_text -> { // "장식" 텍스트
                val pos = mViewModel.pagerPos.value!!
                if (pos != 1) { // 두번 동작 안되게
                    shopPagerView.setCurrentItem(1, true)
                }
            }
            else -> {
            }
        }
    }
}