package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.data.User
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
            viewmodel = mViewModel
            clickListener = this@EmailLoginFragment
            blank = ""

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
        mViewModel.clear()
        super.onDestroyView()
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
            R.id.create_account_text -> { // 회원가입 버튼 눌렀을 때
                findNavController().navigate(R.id.action_emailLoginFragment_to_createAccountFragment)
            }
            else -> {
            }
        }
    }

    private fun setObserver() {
        mViewModel.errorMsg.observe(viewLifecycleOwner) { msg ->
            if (msg.isNotEmpty()) {
                when (msg) {
                    ErrorMessage.SUCCESS -> {
                        mViewModel.getUserData()
                            .addOnSuccessListener {
                                val user = it.toObject(User::class.java)
                                mViewModel.setLoading(false)

                                if (user != null) { // 기존 유저일 때
                                    findNavController().navigate(R.id.action_emailLoginFragment_to_homeFragment)
                                } else {
                                    findNavController().navigate(R.id.createUserFragment)
                                }
                            }
                    }
                    else -> {
                    }
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