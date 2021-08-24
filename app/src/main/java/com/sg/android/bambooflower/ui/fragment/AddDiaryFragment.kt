package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.databinding.FragmentAddDiaryBinding
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.addDiaryFrag.AddDiaryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddDiaryFragment : Fragment() {
    private val mViewModel by viewModels<AddDiaryViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private var completeMenu: MenuItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentAddDiaryBinding.inflate(inflater)

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.flowerImage = gViewModel.flower.value!!.image
            this.flowerProgress = gViewModel.user.value!!.progress

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
        inflater.inflate(R.menu.menu_add_diary_fragment, menu)

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
        val user = gViewModel.user.value!! // 작성자
        val flower = gViewModel.flower.value!! // 꽃

        mViewModel.saveDiary(user, flower)
    }
}