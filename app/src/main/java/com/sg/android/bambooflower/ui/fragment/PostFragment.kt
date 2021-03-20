package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.PostAdapter
import com.sg.android.bambooflower.databinding.FragmentPostBinding
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.postFragment.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostFragment : Fragment(), PostAdapter.PostItemListener {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<PostViewModel>()

    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        postAdapter = PostAdapter().apply {
            setPostItemListener(this@PostFragment)
        }
        val binding = FragmentPostBinding.inflate(inflater)

        with(binding) {
            adapter = postAdapter
            navController = findNavController()

            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    override fun onItemClickListener(pos: Int) {
        gViewModel.setPost(postAdapter.getPost(pos)!!)

        findNavController().navigate(R.id.postDialog)
    }

    private fun setObserver() {
        lifecycleScope.launch {
            mViewModel.postList.collect { pagingData ->
                postAdapter.submitData(pagingData)
            }
        }
    }
}