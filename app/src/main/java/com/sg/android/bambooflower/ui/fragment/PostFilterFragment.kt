package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.sg.android.bambooflower.adapter.PostPagingAdapter
import com.sg.android.bambooflower.databinding.FragmentPostFilterBinding
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.postFilterFrag.PostFilterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostFilterFragment(private val isFiltering: Boolean) : Fragment(), PostPagingAdapter.PostItemListener {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<PostFilterViewModel>()

    private lateinit var postAdapter: PostPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentPostFilterBinding.inflate(inflater)
        postAdapter = PostPagingAdapter().apply {
            setOnPostItemListener(this@PostFilterFragment)
        }

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.postList.adapter = postAdapter

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver() // 옵저버 설정

        postAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh !is LoadState.Loading
                && mViewModel.postList.value != null
            ) {
                mViewModel.isLoading.value = false
            }
        }
    }

    // 아이템 클릭
    override fun onItemClickListener(pos: Int) {
        gViewModel.post.value = postAdapter.getPost(pos)!!
    }

    // 옵저버 설정
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
                mViewModel.postList.value = null
                mViewModel.syncPost(isFiltering = isFiltering)
            } else {
                mViewModel.size.value = postAdapter.itemCount
            }
        }
    }
}