package com.sg.android.bambooflower.ui.dialog

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.data.Flower
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
    private lateinit var motionLayoutView: MotionLayout
    private lateinit var completeLayoutView: ConstraintLayout

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
            motionLayoutView = motionLayout
            completeLayoutView = completeLayout

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
                // 업데이트 된 진행도
                val progress = if (gViewModel.user.value!!.progress == 0) {
                    100
                } else {
                    gViewModel.user.value!!.progress
                }

                if (progress == 100 && completeLayoutView.alpha == 0f) {
                    motionLayoutView.transitionToEnd()
                    mViewModel.rewardMoney.value = 500
                } else {
                    findNavController().navigateUp()
                }
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
                // 현재 꽃 이미지 설정
                mViewModel.currentFlowerImage.value = gViewModel.flower.value!!.image
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
        // 업데이트 된 진행도
        val toProgress = if (gViewModel.user.value!!.progress == 0) {
            100
        } else {
            gViewModel.user.value!!.progress
        }
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
                if ((animator.animatedValue as Int) == 100) { // 꽃을 다 성장시켰을 때
                    // 유저의 꽃 데이터를 씨앗으로 수정함
                    val editFlower = Flower(
                        1,
                        "씨앗",
                        0,
                        mViewModel.flowerImages.value!![0]
                    )

                    // 현재 보여주는 꽃 이미지를 마지막 상태 이미지를 보여줌
                    mViewModel.currentFlowerImage.value =
                        mViewModel.flowerImages.value!![3]
                    // 유저의 꽃 데이터를 수정
                    gViewModel.flower.value = editFlower
                } else if ((animator.animatedValue as Int) >= 50 &&
                    flowerData.state == 1
                ) { // 꽃을 절반이상 성장시켰을 때
                    val flowerImage = mViewModel.flowerImages.value!![2]
                    // 유저의 꽃 데이터를 중간 상태로 수정함
                    val editFlower = Flower(
                        id = flowerData.id + 1,
                        name = flowerData.name,
                        state = flowerData.state + 1,
                        image = flowerImage
                    )

                    // 현재 보여주는 꽃 이미지를 중간 상태 이미지를 보여줌
                    mViewModel.currentFlowerImage.value = flowerImage
                    // 유저의 꽃 데이터를 수정
                    gViewModel.flower.value = editFlower
                }
            }

            it.addListener(object : AnimatorListenerAdapter() {
                // 애니메이션이 끝날때
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    mViewModel.isEnable.value = true // 버튼 활성화
                }
            })

            it.start()
        }
    }
}