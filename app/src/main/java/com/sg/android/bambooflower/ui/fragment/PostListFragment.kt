package com.sg.android.bambooflower.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
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
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
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
class PostListFragment : Fragment(), PostPagingAdapter.PostItemListener {
    @Inject
    lateinit var assistedFactory: PostListViewModel.AssistedFactory
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<PostListViewModel> {
        PostListViewModel.provideFactory(assistedFactory)
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
            setOnPostItemListener(this@PostListFragment)
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
        setObserver()

        postAdapter.addLoadStateListener { loadState ->
            // 리스트 로딩 끝
            if (loadState.refresh !is LoadState.Loading
                && mViewModel.postList.value != null
            ) {
                mViewModel.postSize.value = postAdapter.itemCount

                if (mViewModel.isLoading.value!!) {
                    // 로딩 중일 때
                    mViewModel.isLoading.value = false
                } else {
                    // 새로고침 중일 때
                    mViewModel.isRefresh.value = false
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // 툴바설정
        with((activity as MainActivity)) {
            supportActionBar?.title = ""
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            showToolbar()
        }
    }

    override fun onDestroyView() {
        mViewModel.clearData()
        super.onDestroyView()
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
        val postData = postAdapter.getPost(pos)
        val uid = gViewModel.user.value!!.uid

        // 좋아요를 누르지 않았고 자신의 게시글이 아닐경우
        if (!postData.isCheer && postData.userId != uid) {
            mViewModel.pressedCheer(uid, postData)

            postAdapter.notifyItemChanged(pos)
        }
    }

    override fun onShowPeopleClickListener(pos: Int) {
        val postData = postAdapter.getPost(pos)
        gViewModel.post.value = postData

        findNavController().navigate(R.id.cheerListDialog)
    }

    private fun setObserver() {
        // 게시글 리스트
        mViewModel.postList.observe(viewLifecycleOwner) {
            if (it != null) {
                postAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
        // 로드 된 광고
        mViewModel.nativeAd.observe(viewLifecycleOwner) { nativeAd ->
            val isLoading = mViewModel.isLoading.value!!
            if (nativeAd != null && isLoading) {
                // 로드 된 광고가 있을 때 게시글 읽어오기
                mViewModel.getPostList()
            } else if (nativeAd == null) {
                // 로드 된 광고가 없을 때 광고 로드
                setAd()
            }
        }
        // 오류 여부
        mViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                Toast.makeText(requireContext(), ErrorMessage.CONNECT_ERROR, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        mViewModel.mainLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                (requireActivity() as MainActivity).loading()
            } else {
                (requireActivity() as MainActivity).ready()
            }
        }
        // 새로고침 여부
        mViewModel.isRefresh.observe(viewLifecycleOwner) { isRefresh ->
            val isLoading = mViewModel.isLoading.value!!

            if (isRefresh && !isLoading) {
                // 로딩이 다된 후에만 새로고침 가능
                postAdapter.refresh()
            } else if (isRefresh && isLoading) {
                // 로딩이 되기전에 새로고침 하는것을 방지함
                mViewModel.isRefresh.value = false
            }
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

    // 광고 로드
    private fun setAd() {
        // TODO: 출시 전 광고 아이디 수정
        val adLoader = AdLoader
            .Builder(requireContext(), resources.getString(R.string.ad_native_unit_id_test))
            .forNativeAd {
                mViewModel.nativeAd.value = it
            }
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
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
            val uid = gViewModel.user.value!!.uid

            if (postData.userId == uid ||
                uid == "DQ09dobyPqY0SrLHIMcRYmdOdfO2"
            ) {
                menu.getItem(1).isVisible = true
                menu.getItem(2).isVisible = false
            } else {
                menu.getItem(1).isVisible = false
                menu.getItem(2).isVisible = true
            }

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
                    R.id.menu_report_post -> { // 신고하기
                        gViewModel.post.value = postData
                        findNavController().navigate(R.id.reportDialog)

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