package com.sg.android.bambooflower.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.databinding.FragmentCreateUserBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.ui.SecondActivity
import com.sg.android.bambooflower.viewmodel.createUserFragment.CreateUserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateUserFragment : Fragment(R.layout.fragment_create_user), View.OnClickListener {
    private val mViewModel by viewModels<CreateUserViewModel>()
    private val args by navArgs<CreateUserFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentCreateUserBinding.inflate(inflater)
        mViewModel.email.value = args.email
        mViewModel.token.value = args.token
        mViewModel.loginWay.value = args.loginWay

        activity?.onBackPressedDispatcher?.addCallback(backPressed)

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.clickListener = this@CreateUserFragment
            this.blank = ""

            SpannableString("이용약관 동의").let {
                it.setSpan(UnderlineSpan(), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                firstText.text = it
            }
            SpannableString("개인정보 수집 및 이용에 관한 동의").let {
                it.setSpan(UnderlineSpan(), 0, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                secondText.text = it
            }

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        setObserver() // 옵저버 설정
    }

    override fun onStart() {
        super.onStart()
        // 툴바 설정
        with((activity as MainActivity)) {
            supportActionBar?.title = ""
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            showToolbar(false)
        }
    }

    override fun onDestroyView() {
        backPressed.isEnabled = false
        super.onDestroyView()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                exitFragment()
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
            R.id.first_text -> {
                goViewer(Contents.CHILD_TERMS_OF_SERVICE)
            }
            R.id.second_text -> {
                goViewer(Contents.CHILD_PERSONAL_INFORMATION)
            }
            R.id.start_btn -> {
                mViewModel.setUserData()
            }
        }
    }

    // 옵저버 설정
    private fun setObserver() {
        // 완료 여부
        mViewModel.isComplete.observe(viewLifecycleOwner) { isComplete ->
            if (isComplete) {
                findNavController().navigate(R.id.action_createUserFragment_to_missionListFragment)
            }
        }
        // 오류 여부
        mViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                Toast.makeText(requireContext(), "서버와 연결 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT)
                    .show()
                mViewModel.isError.value = false
            }
        }
        // 로딩 여부
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                (requireActivity() as MainActivity).loading()
            } else {
                (requireActivity() as MainActivity).ready()
            }
        }
    }

    // html 읽어오는 화면으로 이동
    private fun goViewer(title: String) {
        val intent = Intent(requireContext(), SecondActivity::class.java).apply {
            putExtra(Contents.EXTRA_TITLE, title)
            action = Contents.SHOW_WEB_VIEWER
        }
        startActivity(intent)
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

    private fun exitFragment() { // 종료
        with(MaterialAlertDialogBuilder(requireContext())) {
            setMessage("회원가입을 종료하시겠습니까?")
            setPositiveButton("확인") { dialog, which ->
                mViewModel.signOut(requireContext())
                findNavController().navigateUp()
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            show()
        }
    }

    // 뒤로가기 동작
    private val backPressed = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {
            exitFragment()
        }
    }
}