package com.sg.android.bambooflower.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sg.android.bambooflower.BuildConfig
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.databinding.FragmentSettingBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.ui.SecondActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.settingFragment.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment(), View.OnClickListener {
    private val mViewModel by viewModels<SettingViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentSettingBinding.inflate(inflater)

        // 바인딩 설정
        with(binding) {
            this.clickListener = this@SettingFragment
            versionText.setSettingText("Version ${BuildConfig.VERSION_NAME}")

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
            supportActionBar?.title = "설정"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            showToolbar()
        }
    }

    // 메뉴 설정
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
            R.id.service_btn -> { // 이용약관
                goViewer(Contents.CHILD_TERMS_OF_SERVICE)
            }
            R.id.personal_btn -> { // 개인정보
                goViewer(Contents.CHILD_PRIVACY_POLICY)
            }
            R.id.diary_clear_btn -> { // 일기 모두삭제
                clearDiary()
            }
            R.id.send_email_btn -> { // 문의하기
                sendEmail()
            }
            R.id.sign_out_btn -> { // 로그아웃
                signOut()
            }
            R.id.delete_account_btn -> { // 회원 탈퇴
                deleteAccount()
            }
            else -> {
            }
        }
    }

    private fun setObserver() {
        // 로딩
        mViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                (activity as MainActivity).loading()
            } else {
                (activity as MainActivity).ready()
            }
        }
    }

    // 일기 삭제
    private fun clearDiary() {
        with(MaterialAlertDialogBuilder(requireContext())) {
            setMessage("지금까지 작성하신 일기를 모두 삭제하시겠습니까?")
            setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }
            setPositiveButton("확인") { dialog, which ->
                mViewModel.clearDiary(gViewModel.user.value!!.uid)
            }

            show()
        }
    }

    private fun sendEmail() { // 문의하기
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            val email = arrayOf(getString(R.string.developer_email))

            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, email)
        }

        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun signOut() { // 로그아웃
        gViewModel.user.value = null
        gViewModel.missionList.value = null
        gViewModel.userImage.value = null

        mViewModel.signOut(requireContext())
        findNavController().navigate(R.id.action_settingFragment_to_signUpFragment)
    }

    private fun deleteAccount() { // 계정 삭제
        findNavController().navigate(R.id.action_settingFragment_to_deleteAccountFragment)
    }

    // html 읽어오는 화면으로 이동
    private fun goViewer(title: String) {
        val intent = Intent(requireContext(), SecondActivity::class.java).apply {
            putExtra(Contents.EXTRA_TITLE, title)
            action = Contents.SHOW_WEB_VIEWER
        }
        startActivity(intent)
    }
}