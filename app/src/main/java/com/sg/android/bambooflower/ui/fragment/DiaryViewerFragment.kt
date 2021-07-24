package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.databinding.FragmentDiaryViewerBinding
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.diaryViewerFragment.DiaryViewerViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DiaryViewerFragment : Fragment() {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<DiaryViewerViewModel>()

    private lateinit var diary: Diary

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentDiaryViewerBinding.inflate(inflater)
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

    // 메뉴설정
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_diaryviewer_fragment, menu)
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
                findNavController().navigate(R.id.action_diaryViewerFragment_to_diaryEditFragment)
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

                Toast.makeText(requireContext(), "삭제하였습니다.", Toast.LENGTH_SHORT)
                    .show()
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            show()
        }
    }
}