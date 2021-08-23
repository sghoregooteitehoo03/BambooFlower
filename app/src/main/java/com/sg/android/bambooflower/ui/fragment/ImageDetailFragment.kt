package com.sg.android.bambooflower.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.sg.android.bambooflower.adapter.ImagePagerAdapter
import com.sg.android.bambooflower.databinding.FragmentImageDetailBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.ui.SecondActivity
import com.sg.android.bambooflower.viewmodel.imageDetailFragment.ImageDetailViewModel

class ImageDetailFragment : Fragment() {
    private val mViewModel by viewModels<ImageDetailViewModel>()
    private val pagerCallback = object : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            mViewModel.imagePos.value = position + 1
        }
    }

    private lateinit var imageAdapter: ImagePagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentImageDetailBinding.inflate(inflater)
        val imageList = SecondActivity.images!! // 이미지 리스트
        val imagePos = requireActivity().intent.getIntExtra(Contents.EXTRA_SET_POS, 0)// 이미지 위치

        mViewModel.imagePos.value = imagePos + 1
        imageAdapter = ImagePagerAdapter(imageList)

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.imageSize = imageList.size
            this.activity = requireActivity()

            with(imagePager) {
                adapter = imageAdapter
                setCurrentItem(imagePos, false)
                registerOnPageChangeCallback(pagerCallback)
            }

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        // 툴바 설정
        with((activity as SecondActivity).supportActionBar) {
            this?.hide()
        }

        // Statusbar 설정
        with(requireActivity().window) {
            decorView.systemUiVisibility = 0
            statusBarColor = Color.BLACK
            navigationBarColor = Color.BLACK
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        SecondActivity.images = null // 이미지 리스트 초기화
    }
}