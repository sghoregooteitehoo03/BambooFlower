package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialSharedAxis
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.PostPagingAdapter
import com.sg.android.bambooflower.databinding.FragmentPostListBinding
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.postListFragment.PostListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// TODO:
//  . 광고 릴리스 키로 변경

@AndroidEntryPoint
class PostListFragment : Fragment(), PostPagingAdapter.PostItemListener {
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
    }

    override fun onStart() {
        super.onStart()
        // 툴바설정
        with((activity as MainActivity).supportActionBar) {
            this?.title = "인증 게시판"
            this?.show()
            this?.setDisplayHomeAsUpEnabled(true)
        }
    }

    // 메뉴 설정
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.menu_postlist_fragment, menu)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
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
                lifecycleScope.launch {
                    postAdapter.loadStateFlow.collect {
                        if (it.refresh !is LoadState.Loading) {
                            mViewModel.isLoading.value = false
                        }
                    }
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
        gViewModel.syncData.observe(viewLifecycleOwner) {
            if (it) {
                mViewModel.isLoading.value = true
            }
        }
    }
}