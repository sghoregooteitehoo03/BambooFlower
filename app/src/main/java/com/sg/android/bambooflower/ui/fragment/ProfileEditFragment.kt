package com.sg.android.bambooflower.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
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
import com.sg.android.bambooflower.databinding.FragmentProfileEditBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.profileEditFrag.ProfileEditViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileEditFragment : Fragment(), View.OnClickListener {
    private val mViewModel by viewModels<ProfileEditViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentProfileEditBinding.inflate(inflater)
        user = gViewModel.user.value!!
        mViewModel.profileImage.value = user.profileImage
        mViewModel.email.value = user.email
        mViewModel.name.value = user.name

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.clickListener = this@ProfileEditFragment
            this.blank = ""

            this.lifecycleOwner = viewLifecycleOwner
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
            supportActionBar?.title = "프로필 수정"
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

    // 버튼 액션
    override fun onClick(v: View) {
        when (v.id) {
            R.id.profile_image -> {
                getImage()
            }
            R.id.edit_profile_btn -> {
                mViewModel.changeProfile(user)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Contents.GET_IMAGE) {
                mViewModel.profileImage.value = data?.data?.toString()
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

    // 옵저버 설정
    private fun setObserver() {
        // 로딩 여부
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                (requireActivity() as MainActivity).loading()
            } else {
                (requireActivity() as MainActivity).ready()
            }
        }
        // 프로필 업데이트 여부
        mViewModel.isUpdate.observe(viewLifecycleOwner) { isUpdate ->
            if (isUpdate) {
                gViewModel.user.value = user
                gViewModel.userImage.value = user.profileImage

                findNavController().navigateUp()
            }
        }
        mViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                Toast.makeText(requireContext(), ErrorMessage.CONNECT_ERROR, Toast.LENGTH_SHORT)
                    .show()
                mViewModel.isError.value = false
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

    private fun getImageIntent() { // 인텐트를 만들어 실행 함
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_PICK
        }
        startActivityForResult(intent, Contents.GET_IMAGE)
    }

    // 권한 체크
    private fun checkPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) != PackageManager.PERMISSION_GRANTED
}