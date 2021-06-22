package com.sg.android.bambooflower.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sg.android.bambooflower.databinding.DialogReportBinding
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.reportDialog.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportDialog : BottomSheetDialogFragment() {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<ReportViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = DialogReportBinding.inflate(inflater)

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.post = gViewModel.post.value
            this.navController = findNavController()
            this.blank = ""

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    // 옵저버 설정
    private fun setObserver() {
        mViewModel.isReported.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(requireContext(), "신고가 접수되었습니다.", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigateUp()
            }
        }
    }
}