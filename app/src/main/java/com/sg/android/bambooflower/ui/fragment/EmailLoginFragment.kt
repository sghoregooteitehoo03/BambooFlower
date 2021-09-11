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
import com.sg.android.bambooflower.databinding.FragmentEmailLoginBinding
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.emailLoginFragment.EmailLoginViewModel
import dagger.hilt.android.AndroidEntryPoint

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
        with((activity as MainActivity)) {
            supportActionBar?.title = ""
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            showToolbar(false)
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
                findNavController().navigate(R.id.action_emailLoginFragment_to_resetPasswordFragment)
            }
            else -> {
            }
        }
    }

    private fun setObserver() {
        // 로그인 성공 여부
        mViewModel.isLoginSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) { // 로그인 성공 시
                mViewModel.checkUserData()
            }
        }
        // 유저 데이터 존재 여부
        mViewModel.isExist.observe(viewLifecycleOwner) { isExist ->
            if (isExist != null) {
                if (isExist) { // 유저 데이터가 존재할 때
                    findNavController().navigate(R.id.action_emailLoginFragment_to_homeFragment)
                } else { // 존재하지 않을 때
                    val directions = EmailLoginFragmentDirections
                        .actionEmailLoginFragmentToCreateUserFragment(
                            "",
                            mViewModel.email.value!!,
                            "Email"
                        )
                    findNavController().navigate(directions)
                }

                mViewModel.isExist.value = null // 초기화
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
    }
}