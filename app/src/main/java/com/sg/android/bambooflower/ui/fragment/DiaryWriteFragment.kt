package com.sg.android.bambooflower.ui.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.databinding.FragmentDiaryWriteBinding
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.diaryWriteFragment.DiaryWriteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiaryWriteFragment : Fragment() {
    private val mViewModel by viewModels<DiaryWriteViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    private var completeMenu: MenuItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentDiaryWriteBinding.inflate(inflater)
        gViewModel.satisfaction.value =
            BitmapFactory.decodeResource(requireContext().resources, R.drawable.low_image) // 기본값 설정

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.gviewmodel = gViewModel
            this.navController = findNavController()

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        findNavController().navigate(R.id.satisfactionFragment) // 만족도 화면으로 넘어감
        setObserver()
    }

    override fun onStart() {
        super.onStart()
        // 툴바설정
        with((activity as MainActivity).supportActionBar) {
            this?.show()
            this?.title = "일기 작성"
            this?.setDisplayHomeAsUpEnabled(true)
        }
        (activity as MainActivity).hideSatisfaction()
    }

    override fun onDestroyView() {
        gViewModel.satisfaction.value = null // 초기값으로 바꿔놓음
        super.onDestroyView()
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

    // 옵저버 설정
    private fun setObserver() {
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
        val satisfactionBitmap = gViewModel.satisfaction.value!!

        mViewModel.saveDiary(uid, satisfactionBitmap)
    }
}