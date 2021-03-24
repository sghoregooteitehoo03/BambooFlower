package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sg.android.bambooflower.databinding.FragmentDiaryViewerBinding
import com.sg.android.bambooflower.viewmodel.GlobalViewModel

class DiaryViewerFragment : Fragment() {
    private val gViewModel by activityViewModels<GlobalViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDiaryViewerBinding.inflate(inflater)
        with(binding) {
            diaryData = gViewModel.diary.value
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        gViewModel.diary.value = null
        super.onDestroyView()
    }
}