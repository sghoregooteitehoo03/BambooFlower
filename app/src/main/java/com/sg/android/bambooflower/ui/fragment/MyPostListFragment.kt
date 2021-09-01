package com.sg.android.bambooflower.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.PostPagingAdapter
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.databinding.FragmentPostListBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.ui.SecondActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.postListFragment.PostListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyPostListFragment : Fragment(), PostPagingAdapter.PostItemListener {
    @Inject
    lateinit var assistedFactory: PostListViewModel.AssistedFactory
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<PostListViewModel> {
        PostListViewModel.provideFactory(assistedFactory, true)
    }

    private lateinit var postAdapter: PostPagingAdapter
    private lateinit var mainLayoutView: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentPostListBinding.inflate(inflater)
        postAdapter = PostPagingAdapter().apply {
            setOnPostItemListener(this@MyPostListFragment)
        }

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.postList.adapter = postAdapter
            mainLayoutView = this.mainLayout

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
        with((activity as MainActivity)) {
            supportActionBar?.title = "인증 활동"
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

    // 더보기 버튼 클릭
    override fun onMoreItemClickListener(pos: Int, view: View) {
        val postData = postAdapter.getPost(pos)
        showPopup(postData, view) // 팝업을 보여줌
    }

    // 게시글 이미지 클릭
    override fun onImageClickListener(pos: Int) {
        val postImage = postAdapter.getPost(pos)
            .image
        showImage(postImage)
    }

    // 좋아요 클릭
    override fun onFavoriteClickListener(pos: Int) {
        // 동작 X
    }

    override fun onShowPeopleClickListener(pos: Int) {
        val postData = postAdapter.getPost(pos)
        gViewModel.post.value = postData

        findNavController().navigate(R.id.cheerListDialog)
    }

    private fun setObserver() {
        // 오류 여부
        mViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                Toast.makeText(requireContext(), ErrorMessage.CONNECT_ERROR, Toast.LENGTH_SHORT)
                    .show()
                mViewModel.isError.value = false
            }
        }
        mViewModel.mainLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                (requireActivity() as MainActivity).loading()
            } else {
                (requireActivity() as MainActivity).ready()
            }
        }
        // 로딩 여부
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading && mViewModel.postList.value != null) {
                postAdapter.refresh()
            }
        }
        // 게시글 리스트
        mViewModel.postList.observe(viewLifecycleOwner) {
            postAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
        // 삭제 여부
        mViewModel.isDeleted.observe(viewLifecycleOwner) { isDeleted ->
            if (isDeleted) {
                Snackbar.make(
                    mainLayoutView,
                    "게시글을 삭제하였습니다.",
                    Snackbar.LENGTH_SHORT
                ).show()

                gViewModel.isSyncProfile.value = true // 프로필 갱신
                mViewModel.isDeleted.value = false
            }
        }
    }

    // 이미지 더보기 화면으로 이동
    private fun showImage(image: String) {
        SecondActivity.images = listOf(image)

        val intent = Intent(requireContext(), SecondActivity::class.java).apply {
            action = Contents.SHOW_IMAGE_FRAG
        }
        startActivity(intent)
    }

    // 팝업 표시
    private fun showPopup(postData: Post, view: View) {
        with(PopupMenu(requireContext(), view)) {
            menuInflater.inflate(R.menu.menu_post_fragment, this.menu)
            menu.getItem(1).isVisible = true
            menu.getItem(2).isVisible = false

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_show_quest -> { // 퀘스트 보기
                        gViewModel.post.value = postData
                        findNavController().navigate(R.id.questDialog)

                        true
                    }
                    R.id.menu_delete_post -> { // 삭제하기
                        deletePost(postData)
                        true
                    }
                    else -> false
                }
            }

            show()
        }
    }

    // 게시글 삭제
    private fun deletePost(postData: Post) {
        with(MaterialAlertDialogBuilder(requireContext())) {
            setMessage("게시글을 삭제하시겠습니까?")
            setPositiveButton("확인") { dialog, which ->
                mViewModel.deletePost(postData)
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            show()
        }
    }
}