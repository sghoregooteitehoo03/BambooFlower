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

@AndroidEntryPoint
class MyPostListFragment : Fragment(), PostPagingAdapter.PostItemListener {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<PostListViewModel>()

    private lateinit var postAdapter: PostPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        postAdapter = PostPagingAdapter().apply {
            setOnPostItemListener(this@MyPostListFragment)
        }
        val binding = FragmentPostListBinding.inflate(inflater)

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.navController = findNavController()

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
            R.id.menu_search -> {
                findNavController().navigate(R.id.postSearchFragment)
                true
            }
            else -> false
        }
    }

    // 아이템 클릭
    override fun onItemClickListener(pos: Int) {
        gViewModel.post.value = postAdapter.getPost(pos)!!

        findNavController().navigate(R.id.postFragment)
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
            }
        }
        // 로딩 여부
        mViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                val user = gViewModel.user.value!!
                mViewModel.syncPost(user.uid!!)
            }
        }
    }
}