package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.PostFilterPagerAdapter
import com.sg.android.bambooflower.databinding.FragmentPostListBinding
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.postListFragment.PostListViewModel
import dagger.hilt.android.AndroidEntryPoint

// TODO:
//  . 광고 릴리스 키로 변경

@AndroidEntryPoint
class PostListFragment : Fragment(), View.OnClickListener {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<PostListViewModel>()

    private lateinit var postPagerAdapter: PostFilterPagerAdapter
    private lateinit var postPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentPostListBinding.inflate(inflater)
        postPagerAdapter =
            PostFilterPagerAdapter(mViewModel.fragList.value!!, requireActivity().supportFragmentManager, lifecycle)

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.clickListener = this@PostListFragment
            with(filterPager) {
                postPager = this
                adapter = postPagerAdapter
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        mViewModel.isFiltering.value = position == 1
                    }
                })
            }

            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    override fun onStart() {
        super.onStart()
        // 툴바설정
        with((activity as MainActivity).supportActionBar) {
            this?.title = "인증 게시판"
            this?.show()
            this?.setDisplayHomeAsUpEnabled(false)
        }
    }

    // 뷰 클릭 액션
    override fun onClick(v: View) {
        when (v.id) {
            R.id.all_post_text -> { // "전체" 텍스트
                if (mViewModel.isFiltering.value!!) { // 두번 동작 안되게
                    postPager.setCurrentItem(0, true)
                }
            }
            R.id.filter_post_text -> { // "인증 전" 텍스트
                if (!mViewModel.isFiltering.value!!) { // 두번 동작 안되게
                    postPager.setCurrentItem(1, true)
                }
            }
            else -> {
            }
        }
    }

    private fun setObserver() {
        // 화면 이동 액션
        gViewModel.action.observe(viewLifecycleOwner) { action ->
            if (action.isNotEmpty()) {
                when (action) {
                    "Report" -> {
                        findNavController().navigate(R.id.action_postListFragment_to_reportDialog)
                    }
                    "ShowPeople" -> {
                        findNavController().navigate(R.id.action_postListFragment_to_acceptListDialog)
                    }
                    else -> {
                    }
                }

                gViewModel.action.value = "" // 초기화
            }
        }
        // 게시글 갱신 여부
        gViewModel.syncData.observe(viewLifecycleOwner) {
            if (it) {
                mViewModel.fragList.value = listOf(
                    PostFilterFragment(false),
                    PostFilterFragment(true)
                )
                postPagerAdapter.syncData(mViewModel.fragList.value!!)

                gViewModel.syncData.value = false
            }
        }
    }
}