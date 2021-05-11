package com.sg.android.bambooflower.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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

@AndroidEntryPoint
class ProfileFragment : Fragment(), View.OnClickListener {
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
            this.clickListener = this@ProfileFragment

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
                    gViewModel.syncData.postValue(true)
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Toast.makeText(requireContext(), "권한을 허용해주세요.", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        getImage()
                    }
                } else {
                    if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        || checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    ) {
                        Toast.makeText(requireContext(), "권한을 허용해주세요.", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        getImage()
                    }
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    // 버튼 액션
    override fun onClick(v: View) {
        when (v.id) {
            R.id.profile_image -> {
                getImage()
            }
            R.id.sign_out_btn -> {
                signOut()
            }
            else -> {
            }
        }
    }

    private fun setObserver() {
        mViewModel.isLoading.observe(viewLifecycleOwner) { // 로딩 여부
            if (it) {
                (requireActivity() as MainActivity).loading()
            } else {
                (requireActivity() as MainActivity).ready()
            }
        }
    }

    private fun getImage() { // 갤러리에서 이미지를 선택함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) { // 권한 설정
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Contents.PERMISSION_CODE
                )
            } else {
                getImageIntent()
            }
        } else {
            if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                || checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), Contents.PERMISSION_CODE
                )
            } else {
                getImageIntent()
            }
        }
    }

    private fun getImageIntent() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_PICK
        }
        startActivityForResult(intent, Contents.GET_IMAGE)
    }

    private fun signOut() { // 로그아웃
        findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        mViewModel.signOut()
    }

    // 권한 체크
    private fun checkPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) != PackageManager.PERMISSION_GRANTED
}