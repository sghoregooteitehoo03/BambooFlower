package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sg.android.bambooflower.databinding.FragmentSatisfactionBinding
import com.sg.android.bambooflower.viewmodel.GlobalViewModel

class SatisfactionFragment : BottomSheetDialogFragment() {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private var isTwice = false // 첫 시작 때 옵저버 무시하기 위한 변수

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSatisfactionBinding.inflate(inflater)
        binding.gviewmodel = gViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    private fun setObserver() {
        // 만족도 선택
        gViewModel.satisfaction.observe(viewLifecycleOwner) {
            if (isTwice) {
                findNavController().navigateUp()
            }

            isTwice = true
        }
    }
}