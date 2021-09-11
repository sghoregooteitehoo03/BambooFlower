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
import com.sg.android.bambooflower.BuildConfig
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.databinding.FragmentSettingBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.ui.SecondActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.settingFragment.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint

// TODO:
//  . View 표시 O
//  . 스위치 기능 구현 O
//  . 알림기능 O
//  . 처음 시작 시 배터리 설정하기 X
//  . 로그인 및 계정 생성 시 db에 알림토큰 전달하기 O
//  . 도움말 구현 (나중에)
//  . 회원탈퇴 구현 (나중에)
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
            this.viewmodel = mViewModel
            this.clickListener = this@SettingFragment
            versionText.setSettingText("Version ${BuildConfig.VERSION_NAME}")

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // 세팅값 설정
        mViewModel.setUserSwitches(
            gViewModel.user.value!!.uid
        )
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
            R.id.quest_alarm_switch -> { // 퀘스트 보상 알림
                val uid = gViewModel.user.value!!.uid
                mViewModel.setQuestSwitch(uid)
            }
            R.id.diary_alarm_switch -> { // 일기 알림
                val uid = gViewModel.user.value!!.uid
                mViewModel.setDiarySwitch(uid)
            }
            R.id.send_email_btn -> { // 문의하기
                sendEmail()
            }
            R.id.help_btn -> { // 도움말

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
        gViewModel.flower.value = null

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