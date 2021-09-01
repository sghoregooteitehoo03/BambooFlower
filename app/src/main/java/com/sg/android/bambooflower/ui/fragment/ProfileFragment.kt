package com.sg.android.bambooflower.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.CollectionAdapter
import com.sg.android.bambooflower.adapter.PostImageAdapter
import com.sg.android.bambooflower.databinding.FragmentProfileBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.ui.SecondActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.profileFragment.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

// TODO
//  . 인벤토리 테이블 구현 O
//  . View 표시 O
//  . 갱신 기능 구현 O
//  . 꽃 컬렉션 더보기 구현 (상점 구현 후 구현하기)
//  . 프로필 수정 구현 O.
//  . 게시글 이미지, 더보기 클릭 시 동작 구현 O
//  . 활동 없을 때 보여줄 사진 O
@AndroidEntryPoint
class ProfileFragment : Fragment(), View.OnClickListener, PostImageAdapter.PostImageItemListener {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<ProfileViewModel>()

    private lateinit var collectionAdapter: CollectionAdapter
    private lateinit var postImageAdapter: PostImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentProfileBinding.inflate(inflater)
        collectionAdapter = CollectionAdapter()
        postImageAdapter = PostImageAdapter().apply {
            setOnItemListener(this@ProfileFragment)
        }

        with(binding) {
            this.viewmodel = mViewModel
            this.gviewmodel = gViewModel
            this.clickListener = this@ProfileFragment

            this.flowerList.adapter = collectionAdapter
            this.postImageList.adapter = postImageAdapter

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
        // 툴바 설정
        with((activity as MainActivity)) {
            supportActionBar?.title = ""
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            showToolbar()
        }
    }

    // 메뉴 설정
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_profile_frag, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            R.id.setting_menu -> {
                findNavController().navigate(R.id.action_profileFragment_to_settingFragment)
                true
            }
            else -> false
        }
    }

    // 버튼 액션
    override fun onClick(v: View) {
        when (v.id) {
            R.id.profile_image -> { // 프로필 이미지
                findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
            }
            R.id.post_more_text -> { // 인증활동 더보기
                findNavController().navigate(R.id.action_profileFragment_to_myPostListFragment)
            }
        }
    }

    // 인증활동 이미지 클릭 시
    override fun onImageClickListener(pos: Int) {
        val postImages = postImageAdapter.getList().map { post ->
            post.image
        }
        showImages(postImages, pos)
    }

    private fun setObserver() {
        // 유저 활동 업데이트 여부
        gViewModel.isSyncProfile.observe(viewLifecycleOwner) { isSync ->
            if (isSync && !mViewModel.isLoading.value!!) {
                // 로딩이 다 되었을 때 갱신 O

                mViewModel.isRefresh.value = true
                gViewModel.isSyncProfile.value = false
            } else if (isSync && mViewModel.isLoading.value!!) {
                // 로딩이 다 되지 않았을 때는 갱신 X

                gViewModel.isSyncProfile.value = false
            }
        }
        // 에러 여부
        mViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                Toast.makeText(requireContext(), ErrorMessage.CONNECT_ERROR, Toast.LENGTH_SHORT)
                    .show()
                mViewModel.isError.value = false
            }
        }
        // 로딩 여부
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                val uid = gViewModel.user.value!!.uid
                mViewModel.getProfileData(uid)
            }
        }
        // 갱신 여부
        mViewModel.isRefresh.observe(viewLifecycleOwner) { isRefresh ->
            if (isRefresh) {
                val uid = gViewModel.user.value!!.uid
                mViewModel.getProfileData(uid)
            }
        }
        // 꽃 컬렉션
        mViewModel.flowerList.observe(viewLifecycleOwner) { list ->
            collectionAdapter.syncData(list)
        }
        // 인증 활동
        mViewModel.postList.observe(viewLifecycleOwner) { list ->
            postImageAdapter.syncData(list)
            mViewModel.postSize.value = postImageAdapter.itemCount
        }
    }

    // 이미지 액티비티로 이동
    private fun showImages(images: List<String>, pos: Int) {
        SecondActivity.images = images

        val intent = Intent(requireContext(), SecondActivity::class.java).apply {
            putExtra(Contents.EXTRA_SET_POS, pos)
            action = Contents.SHOW_IMAGE_FRAG
        }
        startActivity(intent)
    }
}