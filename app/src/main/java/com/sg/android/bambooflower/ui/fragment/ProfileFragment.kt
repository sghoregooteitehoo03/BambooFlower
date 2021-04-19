package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.PostAdapter
import com.sg.android.bambooflower.adapter.PostPagingAdapter
import com.sg.android.bambooflower.databinding.FragmentProfileBinding
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.profileFragment.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

// TODO: 디자인 및 기능추가

@AndroidEntryPoint
class ProfileFragment : Fragment(), PostPagingAdapter.PostItemListener {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<ProfileViewModel>()

    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        postAdapter = PostAdapter().apply {
            setOnPostListener(this@ProfileFragment)
        }
        val binding = FragmentProfileBinding.inflate(inflater)

        with(binding) {
            userData = gViewModel.user.value
            myPostList.adapter = postAdapter

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.getPreviewPost()
        setObserver()
    }

    // 게시글 클릭
    override fun onItemClickListener(pos: Int) {
        gViewModel.post.value = postAdapter.getItem(pos)
        findNavController().navigate(R.id.postFragment)
    }

    private fun setObserver() {
        mViewModel.myPosts.observe(viewLifecycleOwner) {
            postAdapter.syncData(it)
        }
    }
}