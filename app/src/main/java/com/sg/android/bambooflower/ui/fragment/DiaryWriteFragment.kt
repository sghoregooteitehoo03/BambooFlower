package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.WeatherPagerAdapter
import com.sg.android.bambooflower.databinding.FragmentDiaryWriteBinding
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.diaryWriteFragment.DiaryWriteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiaryWriteFragment : Fragment() {
    private val mViewModel by viewModels<DiaryWriteViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val dots = mutableListOf<ImageView>()
    private var completeMenu: MenuItem? = null

    private lateinit var weatherAdapter: WeatherPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentDiaryWriteBinding.inflate(inflater)
        weatherAdapter = WeatherPagerAdapter()

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.weatherViewpager.adapter = weatherAdapter
            setDots(this)

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        setObserver()
    }

    override fun onStart() {
        super.onStart()
        // 툴바설정
        with((activity as MainActivity)) {
            supportActionBar?.title = "일기 작성"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            showToolbar()
        }
    }

    // 메뉴 설정
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_writediary_fragment, menu)

        completeMenu = menu.getItem(0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            R.id.menu_write -> { // 작성 완료
                diaryWrite()
                true
            }
            else -> false
        }
    }

    private fun setDots(binding: FragmentDiaryWriteBinding) {
        for (i in 0 until weatherAdapter.itemCount) {
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

            binding.sliderDots.addView(dots[i], params)
        }

        binding.weatherViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mViewModel.pos.value = position
            }
        })
    }

    // 옵저버 설정
    private fun setObserver() {
        // 페이저 위치
        mViewModel.pos.observe(viewLifecycleOwner) { pos ->
            for (i in 0 until weatherAdapter.itemCount) {
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
                    R.drawable.active_image_dot_shape
                )
            )

            mViewModel.weather.value = weatherAdapter.getItem(pos)
        }
        // 저장 여부
        mViewModel.isSaved.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(requireContext(), "저장되었습니다.", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigateUp()
            }
        }
        // 일기 내용
        mViewModel.contents.observe(viewLifecycleOwner) {
            completeMenu?.isEnabled = it.isNotEmpty()
        }
    }

    // 일기 작성
    private fun diaryWrite() {
        val uid = gViewModel.user.value?.uid!! // 작성자 id
        mViewModel.saveDiary(uid, requireContext())
    }
}