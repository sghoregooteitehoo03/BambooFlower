package com.sg.android.bambooflower.ui.dialog

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.databinding.DialogFlowerStateBinding
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.view.CustomProgressView
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.flowerStateDialog.FlowerStateViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FlowerStateDialog : BottomSheetDialogFragment(), View.OnClickListener {
    private val mViewModel by viewModels<FlowerStateViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    private lateinit var progressView: CustomProgressView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = DialogFlowerStateBinding.inflate(inflater)

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.gviewmodel = gViewModel
            this.clickListener = this@FlowerStateDialog

            progressView = flowerProgress

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)

        setObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // 갱신 트리거
        gViewModel.usersQuestList.value = gViewModel.usersQuestList.value
        gViewModel.user.value = gViewModel.user.value
    }

    // 버튼 액션
    override fun onClick(v: View) {
        when (v.id) {
            R.id.reward_btn -> { // 보상 버튼
                findNavController().navigateUp()
            }
        }
    }

    // 옵저버 설정
    private fun setObserver() {
        // 로딩 여부
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                val idx = gViewModel.usersQuestList.value?.indexOf(
                    gViewModel.usersQuest.value
                ) ?: 0
                val usersQuest = gViewModel.usersQuestList.value?.get(idx)

                // 유저의 보상을 설정한 후 유저의 데이터를 수정함
                mViewModel.getReward(
                    gViewModel.user.value!!,
                    gViewModel.flower.value!!,
                    usersQuest
                )
            } else {
                dialog?.setCancelable(true)
                startProgressAnim()
            }
        }
        // 오류 여부
        mViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                Toast.makeText(requireContext(), ErrorMessage.CONNECT_ERROR, Toast.LENGTH_SHORT)
                    .show()
                mViewModel.isError.value = false
            }
        }
    }

    // 성취도 애니메이션
    private fun startProgressAnim() {
        val toProgress = gViewModel.user.value!!.progress
        val flowerData = gViewModel.flower.value!!

        ObjectAnimator.ofInt(
            progressView.getProgressView(),
            "Progress",
            progressView.getProgressView().progress,
            toProgress
        ).let {
            it.duration = 900
            it.setAutoCancel(true)
            it.interpolator = DecelerateInterpolator()

            it.addUpdateListener { animator ->
                // 꽃 데이터 업데이트
                if ((animator.animatedValue as Int) == 100) {
                    flowerData.state = 3
                    gViewModel.flower.value = flowerData
                } else if ((animator.animatedValue as Int) >= 50 &&
                    gViewModel.flower.value!!.state == 1
                ) {
                    flowerData.state = 2
                    gViewModel.flower.value = flowerData
                }
            }

            it.start()
        }
    }
}