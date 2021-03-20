package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sg.android.bambooflower.databinding.FragmentAddPostBinding
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.addPostFragment.AddPostViewModel
import dagger.hilt.android.AndroidEntryPoint

// TODO: 디자인, 이미지도 이용할 수 있게 구현
@AndroidEntryPoint
class AddPostFragment : Fragment() {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<AddPostViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAddPostBinding.inflate(inflater)
        with(binding) {
            viewmodel = mViewModel
            user = gViewModel.user.value

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObserver()
    }

    private fun setObserver() {
        mViewModel.isSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess != null) {
                if (isSuccess) {
                    findNavController().navigateUp()
                } else {
                    Toast.makeText(requireContext(), "빈칸을 모두 채워주세요.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}