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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.databinding.FragmentAddPostBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.addPostFragment.AddPostViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPostFragment : Fragment(), View.OnClickListener {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<AddPostViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentAddPostBinding.inflate(inflater)

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.clickListener = this@AddPostFragment
            this.blank = ""

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
            supportActionBar?.title = "[${gViewModel.usersQuest.value!!.quest.title}]"
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

    // 선택한 이미지를 받아 옴
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Contents.GET_IMAGE && resultCode == Activity.RESULT_OK) {
            mViewModel.image.value = data?.data!! // 이미지 저장
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
            R.id.get_image_layout -> {
                getImage()
            }
            R.id.add_post_btn -> {
                addPost()
            }
            else -> {
            }
        }
    }

    // 옵저버 설정
    private fun setObserver() {
        // 게시글 작성 성공 여부
        mViewModel.isSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                findNavController().navigateUp()
            }
        }
        // 로딩 화면
        mViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                (requireActivity() as MainActivity).loading()
            } else {
                (requireActivity() as MainActivity).ready()
            }
        }
        // 오류 메세지
        mViewModel.errorMsg.observe(viewLifecycleOwner) { msg ->
            if (msg.isNotEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
                    .show()
                mViewModel.errorMsg.value = ""
            }
        }
    }

    // 게시물 게시
    private fun addPost() {
        with(MaterialAlertDialogBuilder(requireContext())) {
            setMessage("내용을 게시하시겠습니까?")
            setPositiveButton("확인") { dialog, which ->
                val uid = gViewModel.user.value!!.uid
                val idx = gViewModel.usersQuestList.value!!.indexOf(
                    gViewModel.usersQuest.value!!
                )
                val usersQuest = gViewModel.usersQuestList.value!![idx]

                mViewModel.addPost(uid, usersQuest, requireContext().contentResolver)
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            show()
        }
    }

    // 이미지를 가져옴
    private fun getImage() {
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
                    ),
                    Contents.PERMISSION_CODE
                )
            } else {
                getImageIntent()
            }
        }
    }

    // 이미지를 가져오는 인텐트를 실행
    private fun getImageIntent() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_PICK
        }
        startActivityForResult(intent, Contents.GET_IMAGE)
    }

    // 권한 확인
    private fun checkPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) != PackageManager.PERMISSION_GRANTED
}