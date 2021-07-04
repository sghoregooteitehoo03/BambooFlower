package com.sg.android.bambooflower.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.databinding.FragmentDiaryWriteBinding
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.diaryWriteFragment.DiaryWriteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiaryEditFragment : Fragment() {
    private val mViewModel by viewModels<DiaryWriteViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private var completeMenu: MenuItem? = null

    private lateinit var diary: Diary // 일기 데이터
    private lateinit var imm: InputMethodManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentDiaryWriteBinding.inflate(inflater)
        imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // 이미 작성된 내용을 넣음
        diary = gViewModel.diary.value!!
        gViewModel.satisfaction.value = diary.satisfaction
        mViewModel.contents.value = diary.contents

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.gviewmodel = gViewModel
            this.navController = findNavController()

            writeDiaryInput.requestFocus()
            imm.toggleSoftInput(0, 0)

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
        with((activity as MainActivity).supportActionBar) {
            this?.show()
            this?.title = "일기 수정"
            this?.setDisplayHomeAsUpEnabled(true)
        }
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
        completeMenu?.isEnabled = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            R.id.menu_write -> { // 수정 완료
                diaryEdit()
                true
            }
            else -> false
        }
    }

    // 옵저버 설정
    private fun setObserver() {
        // 일기 내용
        mViewModel.contents.observe(viewLifecycleOwner) {
            completeMenu?.isEnabled = it.isNotEmpty()
        }
        // 수정된 일기
        mViewModel.editData.observe(viewLifecycleOwner) { editDiary ->
            if (editDiary != null) {
                gViewModel.diary.value = editDiary
                findNavController().navigateUp()

                Toast.makeText(requireContext(), "수정되었습니다.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    // 일기 수정
    private fun diaryEdit() {
        val satisfactionBitmap = gViewModel.satisfaction.value!!
        mViewModel.editDiary(diary, satisfactionBitmap)
    }
}