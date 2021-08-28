package com.sg.android.bambooflower.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.databinding.DialogReportBinding
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.reportDialog.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportDialog : BottomSheetDialogFragment(), View.OnClickListener {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<ReportViewModel>()

    private val reportReasonList = listOf(
        "퀘스트 인증과는 관련 없는 게시글",
        "제시된 인증 방법과는 다르게 인증한 게시글",
        "이전에 인증하였던 사진을 다시 사용한 게시글",
        "불편함을 유발하는 게시글",
        "부적절한 이미지 및 내용이 있는 게시글",
        "홍보성 게시글"
    )

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
            this.reportReason = reportReasonList
            this.clickListener = this@ReportDialog
            this.reportGroup.setOnCheckedChangeListener(radioListener)

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)

        setObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        gViewModel.post.value = null
    }

    // 버튼 액션
    override fun onClick(v: View) {
        when (v.id) {
            R.id.report_button -> {
                val uid = gViewModel.user.value!!.uid
                val postId = gViewModel.post.value!!.id
                val pos = mViewModel.pos.value!!

                // 게시글 신고
                mViewModel.reportPost(
                    uid,
                    postId,
                    reportReasonList[pos]
                )
            }
            R.id.close_btn -> {
                findNavController().navigateUp()
            }
        }
    }

    // 옵저버 설정
    private fun setObserver() {
        // 신고 여부
        mViewModel.isReported.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(requireContext(), "신고가 접수되었습니다.", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigateUp()
            }
        }
        // 오류 메세지
        mViewModel.errorMsg.observe(viewLifecycleOwner) { msg ->
            if (msg.isNotEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigateUp()
            }
        }
        // 로딩 여부
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                dialog?.setCancelable(false)
            } else {
                dialog?.setCancelable(true)
            }
        }
    }

    // 신고 이유 버튼
    private val radioListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->
        when (checkedId) {
            R.id.report_1 -> mViewModel.pos.value = 0
            R.id.report_2 -> mViewModel.pos.value = 1
            R.id.report_3 -> mViewModel.pos.value = 2
            R.id.report_4 -> mViewModel.pos.value = 3
            R.id.report_5 -> mViewModel.pos.value = 4
            R.id.report_6 -> mViewModel.pos.value = 5
        }
    }
}