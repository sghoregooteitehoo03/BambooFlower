package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sg.android.bambooflower.databinding.FragmentDiaryWriteBinding
import com.sg.android.bambooflower.viewmodel.diaryWriteFragment.DiaryWriteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiaryWriteFragment : Fragment() {
    private val mViewModel by viewModels<DiaryWriteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDiaryWriteBinding.inflate(inflater)
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
        mViewModel.isSaved.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(requireContext(), "저장되었습니다.", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigateUp()
            }
        }
    }
}