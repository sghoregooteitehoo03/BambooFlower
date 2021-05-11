package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.databinding.FragmentCreateAccountBinding
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.createUserFragment.CreateUserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateUserFragment : Fragment(R.layout.fragment_create_account) {
    private val mViewModel by viewModels<CreateUserViewModel>()
    private val args by navArgs<CreateUserFragmentArgs>()
    private var fragmentBinding: FragmentCreateAccountBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        initView(view) // 뷰 설정
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

    private fun initView(view: View) {
        val binding = FragmentCreateAccountBinding.bind(view)
        fragmentBinding = binding

        binding.inputId.doOnTextChanged { text, start, before, count ->
            mViewModel.email.value = text.toString()
        }
        binding.inputPasswordLayout.visibility = View.GONE
        binding.inputName.doOnTextChanged { text, start, before, count ->
            mViewModel.name.value = text.toString()
        }

        binding.startBtn.setOnClickListener {
            mViewModel.setUserData(args.token, args.loginWay)
        }
    }

    private fun setObserver() {
        mViewModel.errorMsg.observe(viewLifecycleOwner) { msg ->
            if (msg.isNotEmpty()) {
                when (msg) {
                    ErrorMessage.SUCCESS ->
                        findNavController().navigate(R.id.action_createUserFragment_to_homeFragment)
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
            fragmentBinding!!.startBtn.setCustomEnabled(it.isNotEmpty() && mViewModel.name.value!!.isNotEmpty())
        }
        mViewModel.name.observe(viewLifecycleOwner) {
            fragmentBinding!!.startBtn.setCustomEnabled(mViewModel.email.value!!.isNotEmpty() && it.isNotEmpty())
        }
    }
}