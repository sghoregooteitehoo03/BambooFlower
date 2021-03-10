package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.databinding.FragmentCreateAccountBinding
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.viewmodel.createAccountFragment.CreateAccountViewModel
import dagger.hilt.android.AndroidEntryPoint

// TODO: 디자인 및 자잘한 기능 추가
@AndroidEntryPoint
class CreateAccountFragment : Fragment() {
    private val mViewModel by viewModels<CreateAccountViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCreateAccountBinding.inflate(inflater)
        with(binding) {
            viewmodel = mViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObserver()
    }

    private fun setObserver() {
        mViewModel.errorMsg.observe(viewLifecycleOwner) { msg ->
            if (msg.isNotEmpty()) {
                when (msg) {
                    ErrorMessage.SUCCESS ->
                        findNavController().navigate(R.id.action_createAccountFragment_to_homeFragment)
                    else -> Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}