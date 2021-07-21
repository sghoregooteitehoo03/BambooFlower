package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.MyPhotoPagingAdapter
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.databinding.FragmentProfileBinding
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.profileFragment.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// TODO: 광고 릴리스 키로 변경
//  . 내 게시글 모아보기 구현
//  . 프로필 편집 기능 구현
@AndroidEntryPoint
class ProfileFragment : Fragment(), View.OnClickListener {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<ProfileViewModel>()

    private lateinit var user: User
    private lateinit var myPhotoAdapter: MyPhotoPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentProfileBinding.inflate(inflater)
        user = gViewModel.user.value!!
        myPhotoAdapter = MyPhotoPagingAdapter()

        with(binding) {
            this.viewmodel = mViewModel
            this.gviewmodel = gViewModel

            this.photoList.adapter = myPhotoAdapter

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        setObserver()
        setList()
    }

    override fun onStart() {
        super.onStart()
        // 툴바 설정
        with((activity as MainActivity)) {
            supportActionBar?.title = "프로필"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

    }

    private fun setObserver() {
        mViewModel.isLoading.observe(viewLifecycleOwner) { // 로딩 여부
            if (it) {
                mViewModel.getMyPostList(user.uid!!)
            } else {
                mViewModel.size.value = myPhotoAdapter.itemCount
            }
        }
    }

    private fun setList() {
        lifecycleScope.launch {
            mViewModel.getMyPostList(user.uid!!)
                .collect {
                    myPhotoAdapter.submitData(it)
                }
        }
        myPhotoAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh !is LoadState.Loading) {
                mViewModel.isLoading.value = false
            }
        }
    }
}