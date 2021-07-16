package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.databinding.FragmentEmailLoginBinding
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.emailLoginFragment.EmailLoginViewModel
import dagger.hilt.android.AndroidEntryPoint

// TODO: 이메일 및 비밀번호 넘기는 기능 구현
@AndroidEntryPoint
class EmailLoginFragment : Fragment(), View.OnClickListener {
    private val mViewModel by viewModels<EmailLoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEmailLoginBinding.inflate(inflater)
        with(binding) {
            this.viewmodel = mViewModel
            this.clickListener = this@EmailLoginFragment
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
        with((activity as MainActivity).supportActionBar) {
            this?.show()
            this?.title = "로그인"
            this?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel.clear()
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
            R.id.login_btn -> {
                mViewModel.login()
            }
            R.id.reset_password_text -> {
                // TODO: 비밀번호 재설정 기능 구현
            }
            else -> {
            }
        }
    }

    private fun setObserver() {
        mViewModel.isLoginSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) { // 로그인 성공 시
                mViewModel.getUserData()
                    .addOnSuccessListener {
                        val user = it.toObject(User::class.java)
                        mViewModel.setLoading(false)

                        if (user != null) { // 유저 데이터가 존재할 때
                            findNavController().navigate(R.id.action_emailLoginFragment_to_missionFragment)
                        } else {
                            findNavController().navigate(R.id.createUserFragment)
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "서버와 연결 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        }
        mViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                (requireActivity() as MainActivity).loading()
            } else {
                (requireActivity() as MainActivity).ready()
            }
        }
    }
}