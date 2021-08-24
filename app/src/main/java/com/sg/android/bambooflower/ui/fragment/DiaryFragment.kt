package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.databinding.FragmentDiaryBinding
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.diaryFrag.DiaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DiaryFragment : Fragment() {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<DiaryViewModel>()

    private lateinit var diary: Diary

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentDiaryBinding.inflate(inflater)
        diary = gViewModel.diary.value!!

        // 바인딩 설정
        with(binding) {
            this.diaryData = diary

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onStart() {
        super.onStart()
        // 툴바 설정
        with((activity as MainActivity)) {
            supportActionBar?.title =
                SimpleDateFormat("yy.MM.dd (EE)", Locale.KOREA).format(diary.timeStamp)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            showToolbar()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gViewModel.diary.value = null
    }

    // 메뉴설정
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_diary_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            R.id.menu_delete_diary -> { // 삭제하기
                deleteDiary()
                true
            }
            R.id.menu_edit_diary -> { // 수정하기
                findNavController().navigate(R.id.action_diaryFragment_to_editDiaryFragment)
                true
            }
            else -> false
        }
    }

    // 일기 삭제
    private fun deleteDiary() {
        with(MaterialAlertDialogBuilder(requireContext())) {
            setMessage("정말로 삭제하시겠습니까?")
            setPositiveButton("확인") { dialog, which ->
                mViewModel.deleteDiary(diary)
                findNavController().navigateUp()
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            show()
        }
    }
}