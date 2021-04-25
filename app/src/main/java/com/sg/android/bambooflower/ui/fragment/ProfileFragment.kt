package com.sg.android.bambooflower.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.databinding.FragmentProfileBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.profileFragment.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO:
//  1. 디자인 O (나중에 다시 수정)
//  2. 프로필 사진 변경 기능 O
//  3. 로그아웃 기능 O
//  4. 내 게시글 화면 O
//  5. 수행한 미션 화면 O
//  6. 설정 화면 이동 O

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<ProfileViewModel>()

    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentProfileBinding.inflate(inflater)
        user = gViewModel.user.value!!

        with(binding) {
            this.viewmodel = mViewModel
            this.gviewmodel = gViewModel
            this.navController = findNavController()

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObserver()
    }

    override fun onStart() {
        super.onStart()
        // 툴바 설정
        with((activity as MainActivity).supportActionBar) {
            this?.title = "프로필"
            this?.show()
            this?.setDisplayHomeAsUpEnabled(false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Contents.GET_IMAGE) {
                CoroutineScope(Dispatchers.IO).launch {
                    mViewModel.changeProfileImage(user, data?.data!!)
                    gViewModel.user.postValue(user)
                }
            }
        }
    }

    // 권한 설정
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Contents.PERMISSION_CODE -> {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(requireContext(), "권한을 허용해주세요.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    getImage()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun setObserver() {
        mViewModel.buttonAction.observe(viewLifecycleOwner) { action ->
            if (action.isNotEmpty()) {
                when (action) {
                    Contents.ACTION_GET_IMAGE -> { // 프로필 변경 액션
                        getImage()
                    }
                    Contents.ACTION_LOG_OUT -> { // 로그아웃 액션
                        signOut()
                    }
                    else -> {
                    }
                }

                mViewModel.setButtonAction("")
            }
        }
        mViewModel.isLoading.observe(viewLifecycleOwner) { // 로딩 여부
            if (it) {
                (requireActivity() as MainActivity).loading()
            } else {
                (requireActivity() as MainActivity).ready()
            }
        }
    }

    private fun getImage() { // 갤러리에서 이미지를 선택함
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) { // 권한 설정
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Contents.PERMISSION_CODE
            )
        } else {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_PICK
            }
            startActivityForResult(intent, Contents.GET_IMAGE)
        }
    }

    private fun signOut() { // 로그아웃
        findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        mViewModel.signOut()
    }
}