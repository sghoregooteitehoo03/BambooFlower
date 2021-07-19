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
import com.sg.android.bambooflower.adapter.PostPagingAdapter
import com.sg.android.bambooflower.databinding.FragmentPostFilterBinding
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.postFilterFrag.PostFilterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPostListFragment : Fragment()
//    , PostPagingAdapter.PostItemListener
{
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
        val user = gViewModel.user.value!!
//        postAdapter = PostPagingAdapter(user.uid!!).apply {
//            setOnPostItemListener(this@MyPostListFragment)
//        }

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
            this?.title = "내 게시글"
            this?.show()
            this?.setDisplayHomeAsUpEnabled(true)
        }
    }

    // 메뉴 설정

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> false
        }
    }

    // 아이템 클릭
//    override fun onItemClickListener(pos: Int) {
//
//    }

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
                val user = gViewModel.user.value!!
                mViewModel.postList.value = null // 게시글 리스트 초기화

                mViewModel.syncPost(user.uid!!, false)
            } else {
                mViewModel.size.value = postAdapter.itemCount
            }
        }
        gViewModel.syncData.observe(viewLifecycleOwner) {
            if (it) {
                mViewModel.isLoading.value = true
                gViewModel.syncData.value = false
            }
        }
    }
}