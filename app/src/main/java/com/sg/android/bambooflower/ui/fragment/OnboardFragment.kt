package com.sg.android.bambooflower.ui.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.BoardPagerAdapter
import com.sg.android.bambooflower.databinding.FragmentOnboardBinding
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.onboardFragment.OnboardViewModel

class OnboardFragment : Fragment(), View.OnClickListener {
    private val mViewModel by viewModels<OnboardViewModel>()
    private val dots = mutableListOf<ImageView>()

    private lateinit var fragList: List<Fragment>
    private var maxWidth = 0
    private var minWidth = 0
    private var fragBinding: FragmentOnboardBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // 인스턴스 설정
        fragBinding = FragmentOnboardBinding.inflate(inflater)
        fragList = listOf(BoardFirstFragment(), BoardSecondFragment(), BoardThirdFragment())

        // 바인딩 설정
        with(fragBinding!!) {
            this.clickListener = this@OnboardFragment
            this.viewmodel = mViewModel

            fragViewpager.adapter = BoardPagerAdapter(fragList, requireActivity().supportFragmentManager, lifecycle)

            lifecycleOwner = viewLifecycleOwner
        }

        return fragBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWidth() // 버튼 애니메이션을 위한 세팅
        setDots()

        setObserver()
    }

    override fun onStart() {
        super.onStart()
        // 툴바 설정
        with((activity as MainActivity).supportActionBar) {
            this?.show()
            this?.title = ""
        }
    }

    override fun onDestroy() {
        fragBinding = null
        super.onDestroy()
    }

    // 버튼 액션
    override fun onClick(v: View) {
        when (v.id) {
            R.id.next_btn -> {
                fragBinding!!.fragViewpager.setCurrentItem(mViewModel.position.value!! + 1, true)
            }
            R.id.start_btn -> {
                findNavController().navigate(R.id.action_onboardFragment_to_loginFragment)
            }
        }
    }

    private fun setObserver() {
        mViewModel.position.observe(viewLifecycleOwner) { pos ->
            Log.i("Check", "pos: $pos")
            for (i in fragList.indices) {
                dots[i].setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.non_active_dot_shape
                    )
                )
            }

            dots[pos].setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.active_dot_shape
                )
            )

            startAnimation(pos == fragList.size - 1)
        }
    }

    private fun setDots() {
        for (i in fragList.indices) {
            dots.add(ImageView(requireContext()))
            dots[i].setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.non_active_dot_shape
                )
            )

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 0, 8, 0)
            }

            fragBinding!!.sliderDots.addView(dots[i], params)
        }

        fragBinding!!.fragViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mViewModel.position.value = position
            }
        })
    }

    private fun setWidth() {
        minWidth = fragBinding!!.startBtn.layoutParams.width
        maxWidth = (minWidth + (TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            52f,
            resources.displayMetrics
        ))).toInt()
    }

    // 버튼 확장 및 축소 애니메이션
    private fun startAnimation(isExpand: Boolean) {
        if (isExpand) {
            // 확장
            fragBinding!!.nextBtn.visibility = View.GONE
            fragBinding!!.startBtn.visibility = View.VISIBLE

            val animator = ValueAnimator.ofInt(
                minWidth,
                maxWidth
            ).apply {
                addUpdateListener { animator ->
                    fragBinding!!.startBtn.layoutParams.width = animator.animatedValue as Int
                    fragBinding!!.startBtn.requestLayout()
                }

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        fragBinding!!.startBtn.text = "시작하기"
                    }
                })
            }

            animator.start()
        } else if (fragBinding!!.startBtn.visibility == View.VISIBLE) {
            // 축소
            val animator = ValueAnimator.ofInt(
                maxWidth,
                minWidth
            ).apply {
                addUpdateListener { animator ->
                    fragBinding!!.startBtn.layoutParams.width = animator.animatedValue as Int
                    fragBinding!!.startBtn.requestLayout()
                }

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)
                        fragBinding!!.startBtn.text = ""
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)

                        fragBinding!!.nextBtn.visibility = View.VISIBLE
                        fragBinding!!.startBtn.visibility = View.GONE
                    }
                })
            }

            animator.start()
        }
    }
}