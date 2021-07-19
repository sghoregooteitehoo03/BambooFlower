package com.sg.android.bambooflower.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.PostPagingAdapter
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.databinding.FragmentPostFilterBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.ui.SecondActivity
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
    private lateinit var user: User

    private var favoritePos = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentPostFilterBinding.inflate(inflater)
        user = gViewModel.user.value!!
        postAdapter = PostPagingAdapter(user.uid!!).apply {
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

    // 더보기 버튼 누름
    override fun onMoreItemClickListener(pos: Int, view: View) {
        showPopup(postAdapter.getPost(pos)!!, view)
    }

    // 게시글 이미지 누름
    override fun onImageClickListener(pos: Int) {
        val postData = postAdapter.getPost(pos)
        showImage(postData?.image!!)
    }

    // 좋아요 누름
    override fun onFavoriteClickListener(pos: Int) {
        val postData = postAdapter.getPost(pos)
        favoritePos = pos

        if (user.uid != postData?.uid!!) {
            favoriteAction(postData)
        }
    }

    // 좋아요를 눌러준 사람을 보여줌
    override fun onShowPeopleClickListener(pos: Int) {
        gViewModel.post.value = postAdapter.getPost(pos)
        gViewModel.action.value = "ShowPeople"
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
        mViewModel.isFavorite.observe(viewLifecycleOwner) {
            if (it) {
                postAdapter.notifyItemChanged(favoritePos)

                mViewModel.isFavorite.value = false
                favoritePos = -1
            }
        }
        // 게시글 삭제 여부
        mViewModel.isDeleted.observe(viewLifecycleOwner) { isDeleted ->
            if (isDeleted) {
                mViewModel.isDeleted.value = false
                gViewModel.syncData.value = true
            }
        }
        // 로딩 여부
        mViewModel.isMainLoading.observe(viewLifecycleOwner) {
            if (it) {
                (requireActivity() as MainActivity).loading()
            } else {
                (requireActivity() as MainActivity).ready()
            }
        }
        // 게시글 로딩 여부
        mViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                mViewModel.postList.value = null
                mViewModel.syncPost(isFiltering = isFiltering) // 필터링 여부
            } else {
                mViewModel.size.value = postAdapter.itemCount
            }
        }
        mViewModel.isError.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(requireContext(), "오류가 발생하였습니다.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    // 좋아요 액션
    private fun favoriteAction(postData: Post) {
        with(MaterialAlertDialogBuilder(requireContext())) {
            setMessage("성공으로 인정하시겠습니까?")
            setPositiveButton("확인") { dialog, which ->
                val isFavorite = postData.favorites.containsKey(user.uid)
                if (!isFavorite) { // 좋아요를 눌렀는 지 확인
                    mViewModel.favorite(user, postData)
                } else {
                    Toast.makeText(requireContext(), "이미 인정한 게시글입니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            show()
        }
    }

    // 이미지 액티비티로 이동
    private fun showImage(image: String) {
        // 데이터 형식을 맞추기 위해 배열로 변환
        val images = listOf(image).toTypedArray()

        val intent = Intent(requireContext(), SecondActivity::class.java).apply {
            putExtra(Contents.EXTRA_SET_IMAGE, images)
            action = Contents.SHOW_IMAGE_FRAG
        }
        startActivity(intent)
    }

    // 팝업 표시
    private fun showPopup(postData: Post, view: View) {
        with(PopupMenu(requireContext(), view)) {
            menuInflater.inflate(R.menu.menu_post_fragment, this.menu)

            menu.getItem(0).isVisible = postData.uid == user.uid ||
                    user.uid == "DQ09dobyPqY0SrLHIMcRYmdOdfO2"
            menu.getItem(1).isVisible = postData.uid != user.uid

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_delete_post -> { // 삭제하기
                        deletePost(postData)
                        true
                    }
                    R.id.menu_report_post -> { // 신고하기
                        gViewModel.post.value = postData
                        gViewModel.action.value = "Report"

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