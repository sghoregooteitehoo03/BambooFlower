package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.databinding.FragmentDeleteAccountBinding
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.deleteAccountFrag.DeleteAccountViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAccountFragment : Fragment(), View.OnClickListener {
    private val mViewModel by viewModels<DeleteAccountViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentDeleteAccountBinding.inflate(inflater)
        user = gViewModel.user.value!!

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.clickListener = this@DeleteAccountFragment

            if (user.loginWay != "Email") {
                // 구글 및 페이스북으로 로그인 한 유저일 경우
                subTitleText.text = "\"회원탈퇴\"를 입력해주세요."
                inputView.hint = "회원탈퇴 입력"
            } else {
                // 이메일로 로그인 한 유저일 경우
                subTitleText.text = "해당 계정의 비밀번호를 입력해주세요."
                inputView.hint = "비밀번호 입력"
            }

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
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            showToolbar(false)
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
            R.id.delete_account_btn -> {
                deleteAccount()
            }
            else -> {
            }
        }
    }

    private fun setObserver() {
        mViewModel.isDeleted.observe(viewLifecycleOwner) {
            if (it) { // 계정이 삭제되었을 때
                gViewModel.user.value = null
                gViewModel.userImage.value = null
                gViewModel.missionList.value = null

                findNavController().navigate(R.id.action_deleteAccountFragment_to_signUpFragment)
            }
        }
        // 로딩 여부
        mViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                (requireActivity() as MainActivity).loading()
            } else {
                (requireActivity() as MainActivity).ready()
            }
        }
        // 오류 여부
        mViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                Toast.makeText(requireContext(), ErrorMessage.CONNECT_ERROR, Toast.LENGTH_SHORT)
                    .show()
                mViewModel.isError.value = false
            }
        }
        // 입력 칸
        mViewModel.inputData.observe(viewLifecycleOwner) { data ->
            mViewModel.isEnabled.value = if (user.loginWay != "Email") {
                // 구글 및 페이스북으로 로그인 한 유저일 경우
                data == "회원탈퇴"
            } else {
                // 이메일로 로그인 한 유저일 경우
                data.isNotEmpty()
            }
        }
    }

    // 계정 삭제 확인 다이얼로그
    private fun deleteAccount() {
        with(MaterialAlertDialogBuilder(requireContext())) {
            setMessage("계정을 정말 삭제하시겠습니까?")
            setPositiveButton("확인") { dialog, which ->
                mViewModel.deleteAccount(user, requireContext())
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            show()
        }
    }
}