package com.sg.android.bambooflower.ui.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sg.android.bambooflower.databinding.DialogCalendarBinding
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.calendarDialog.CalendarViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalendarDialog : DialogFragment() {
    private val mViewModel by viewModels<CalendarViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogCalendarBinding.inflate(inflater)
        with(binding) {
            user = gViewModel.user.value
            viewmodel = mViewModel
            navController = findNavController()

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    fun setObserver() {
        mViewModel.position.observe(viewLifecycleOwner) { pos ->
            if(pos != null) {
                if (pos != -1) {
                    gViewModel.searchPosition.value = pos
                    findNavController().navigateUp()
                } else {
                    Toast.makeText(requireContext(), "일기를 찾을 수 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}