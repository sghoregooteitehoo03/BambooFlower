package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.DiaryPagingAdapter
import com.sg.android.bambooflower.databinding.FragmentDiaryListBinding
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.diaryListFrag.DiaryListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// TODO:
//  . View 표시
//  . 첫 리스트에 광고 표시 (나중에)
@AndroidEntryPoint
class DiaryListFragment : Fragment(), DiaryPagingAdapter.DiaryItemListener, View.OnClickListener {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<DiaryListViewModel>()

    private lateinit var diaryAdapter: DiaryPagingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // 인스턴스 설정
        val binding = FragmentDiaryListBinding.inflate(inflater)
        diaryAdapter = DiaryPagingAdapter().apply {
            setOnDiaryItemListener(this@DiaryListFragment)
        }

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.clickListener = this@DiaryListFragment
            this.diaryList.adapter = diaryAdapter

            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        setList()

//        // 광고 로드
//        val adLoader = AdLoader
//            .Builder(requireContext(), resources.getString(R.string.ad_native_unit_id_test))
//            .forNativeAd {
//                mViewModel.loadAd.value = it
//            }
//            .build()
//        adLoader.loadAd(AdRequest.Builder().build())
    }

    override fun onStart() {
        super.onStart()
        // 툴바설정
        with((activity as MainActivity)) {
            supportActionBar?.title = "하루일기"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            showToolbar()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> false
        }
    }

    // 버튼 액션
    override fun onClick(v: View) {
        when (v.id) {
            R.id.add_diary_btn -> { // 일기 작성버튼
                findNavController().navigate(R.id.action_diaryListFragment_to_addDiaryFragment)
            }
        }
    }

    // 일기 아이템 클릭
    override fun onDiaryItemClickListener(pos: Int) {
        val diaryData = diaryAdapter.getDiaryItem(pos)
        gViewModel.diary.value = diaryData

        findNavController().navigate(R.id.action_diaryListFragment_to_diaryFragment)
    }

    private fun setList() {
        lifecycleScope.launch {
            mViewModel.diaries.collect { pagingData ->
                diaryAdapter.submitData(pagingData)
            }
        }

        diaryAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh !is LoadState.Loading) {
                mViewModel.size.value = diaryAdapter.itemCount
            }
        }
    }
}