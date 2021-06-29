package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.PostPagingAdapter
import com.sg.android.bambooflower.databinding.FragmentPostListBinding
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.postListFragment.PostListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// TODO:
//  . 게시글 디자인 O
//  . 게시글 필터링 기능 O
//  . 광고 릴리스 키로 변경

@AndroidEntryPoint
class PostListFragment : Fragment(), PostPagingAdapter.PostItemListener, View.OnClickListener {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<PostListViewModel>()

    private lateinit var postAdapter: PostPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentPostListBinding.inflate(inflater)
        postAdapter = PostPagingAdapter().apply {
            setOnPostItemListener(this@PostListFragment)
        }

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.clickListener = this@PostListFragment

            with(postList) {
                adapter = postAdapter
                addItemDecoration(
                    DividerItemDecoration(
                        requireContext(),
                        LinearLayoutManager.VERTICAL
                    )
                )
            }

            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        setObserver()
        postAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh !is LoadState.Loading
                && mViewModel.postList.value != null
            ) {
                mViewModel.isLoading.value = false
            }
        }
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
                if (mViewModel.isFiltering.value != false) { // 두번 동작 안되게
                    mViewModel.isFiltering.value = false

                    mViewModel.postList.value = null // 게시글 리스트 초기화
                    mViewModel.isLoading.value = true // 리스트 다시 읽어오기
                }
            }
            R.id.filter_post_text -> { // "인증 전" 텍스트
                if (mViewModel.isFiltering.value != true) { // 두번 동작 안되게
                    mViewModel.isFiltering.value = true

                    mViewModel.postList.value = null // 게시글 리스트 초기화
                    mViewModel.isLoading.value = true // 리스트 다시 읽어오기
                }
            }
            else -> {
            }
        }
    }

    // 메뉴 설정
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.menu_postlist_fragment, menu)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_search -> {
                true
            }
            else -> false
        }
    }

    // 아이템 클릭
    override fun onItemClickListener(pos: Int) {
        gViewModel.post.value = postAdapter.getPost(pos)!!

        findNavController().navigate(R.id.action_postListFragment_to_postFragment)
    }

    private fun setObserver() {
        // 게시글 리스트
        mViewModel.postList.observe(viewLifecycleOwner) { postFlow ->
            if (postFlow != null) {
                lifecycleScope.launch {
                    postFlow.collect { pagingData ->
                        postAdapter.submitData(pagingData)
                    }
                }
            } else {
                lifecycleScope.launch {
                    postAdapter.submitData(PagingData.empty()) // 리스트뷰를 초기화 함
                }
            }
        }
        // 로딩 여부
        mViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                mViewModel.syncPost()
            } else {
                mViewModel.size.value = postAdapter.itemCount
            }
        }
        // 게시글 갱신 여부
        gViewModel.syncData.observe(viewLifecycleOwner) {
            if (it) {
                mViewModel.postList.value = null // 게시글 리스트 초기화

                mViewModel.isLoading.value = true
                gViewModel.syncData.value = false
            }
        }
    }
}