package com.sg.android.bambooflower.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.MenuItem
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.databinding.FragmentCreateAccountBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.ui.SecondActivity
import com.sg.android.bambooflower.viewmodel.createAccountFragment.CreateAccountViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountFragment : Fragment(R.layout.fragment_create_account) {
    private val mViewModel by viewModels<CreateAccountViewModel>()
    private var fragmentBinding: FragmentCreateAccountBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        initView(view) // 뷰 초기화
        setObserver() // 옵저버 설정
    }

    override fun onStart() {
        super.onStart()
        // 툴바 설정
        with((activity as MainActivity).supportActionBar) {
            this?.title = "회원가입"
            this?.show()
            this?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onDestroyView() {
        fragmentBinding = null
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

    // 뷰 초기화
    private fun initView(view: View) {
        val binding = FragmentCreateAccountBinding.bind(view)
        fragmentBinding = binding

        binding.inputId.doOnTextChanged { text, start, before, count ->
            mViewModel.email.value = text.toString()
        }
        binding.inputPassword.doOnTextChanged { text, start, before, count ->
            mViewModel.password.value = text.toString()
        }
        binding.inputName.doOnTextChanged { text, start, before, count ->
            mViewModel.name.value = text.toString()
        }
        binding.firstCheck.setOnCheckedChangeListener { buttonView, isChecked ->
            mViewModel.firstCheck.value = isChecked
        }
        binding.secondCheck.setOnCheckedChangeListener { buttonView, isChecked ->
            mViewModel.secondCheck.value = isChecked
        }
        with(binding.firstText) {
            val span = SpannableString("이용약관 동의").apply {
                setSpan(UnderlineSpan(), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            text = span
            setOnClickListener {
                goViewer(Contents.CHILD_TERMS_OF_SERVICE)
            }
        }
        with(binding.secondText) {
            val span = SpannableString("개인정보 수집 및 이용에 관한 동의").apply {
                setSpan(UnderlineSpan(), 0, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            text = span
            setOnClickListener {
                goViewer(Contents.CHILD_PERSONAL_INFORMATION)
            }
        }

        binding.startBtn.setOnClickListener {
            mViewModel.createAccount()
        }
    }

    private fun setObserver() {
        mViewModel.errorMsg.observe(viewLifecycleOwner) { msg ->
            if (msg.isNotEmpty()) {
                when (msg) {
                    ErrorMessage.SUCCESS ->
                        findNavController().navigate(R.id.action_createAccountFragment_to_homeFragment)
                    else -> {
                        fragmentBinding!!.errorMsgText.text = msg
                    }
                }
            }
        }
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) { // 로딩 중
                (requireActivity() as MainActivity).loading()
            } else {
                (requireActivity() as MainActivity).ready()
            }
        }
        mViewModel.email.observe(viewLifecycleOwner) {
            fragmentBinding!!.startBtn.setCustomEnabled(mViewModel.checkAbleData())
        }
        mViewModel.password.observe(viewLifecycleOwner) {
            fragmentBinding!!.startBtn.setCustomEnabled(mViewModel.checkAbleData())
        }
        mViewModel.name.observe(viewLifecycleOwner) {
            fragmentBinding!!.startBtn.setCustomEnabled(mViewModel.checkAbleData())
        }
        mViewModel.firstCheck.observe(viewLifecycleOwner) {
            fragmentBinding!!.startBtn.setCustomEnabled(mViewModel.checkAbleData())
        }
        mViewModel.secondCheck.observe(viewLifecycleOwner) {
            fragmentBinding!!.startBtn.setCustomEnabled(mViewModel.checkAbleData())
        }
    }

    private fun goViewer(title: String) {
        val intent = Intent(requireContext(), SecondActivity::class.java).apply {
            putExtra(Contents.EXTRA_TITLE, title)
            action = Contents.SHOW_WEB_VIEWER
        }
        startActivity(intent)
    }
}