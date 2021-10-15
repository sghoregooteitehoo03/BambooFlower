package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.DiaryPagingAdapter
import com.sg.android.bambooflower.databinding.FragmentDiaryListBinding
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.diaryListFrag.DiaryListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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

        setObserver() // 옵저버 설정
        checkReward()

        diaryAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh !is LoadState.Loading) {
                mViewModel.size.value = diaryAdapter.itemCount
            }
        }
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
        return when (item.itemId) {
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
                addDiary()
            }
        }
    }

    // 일기 아이템 클릭
    override fun onDiaryItemClickListener(pos: Int) {
        val diaryData = diaryAdapter.getDiaryItem(pos)
        gViewModel.diary.value = diaryData

        findNavController().navigate(R.id.action_diaryListFragment_to_diaryFragment)
    }

    private fun setObserver() {
        lifecycleScope.launch {
            mViewModel.diaries.collect { pagingData ->
                diaryAdapter.submitData(pagingData)
            }
        }
        // 로드 된 광고
        mViewModel.nativeAd.observe(viewLifecycleOwner) { nativeAd ->
            if (nativeAd != null) { // 로드 된 광고가 있을 때
                diaryAdapter.refresh()
            } else { // 로드 된 광고가 없을 때
                setAd() // 광고 로드
            }
        }
    }

    private fun addDiary() {
        val flowerState = gViewModel.flower.value!!.state

        if (flowerState != 0) { // 유저가 성장시키고 있는 꽃이 존재할 때
            findNavController().navigate(R.id.action_diaryListFragment_to_addDiaryFragment)
        } else {
            gViewModel.moveLayout.value = R.id.addDiaryFragment // 선택 후 일기작성화면으로 이동되게 설정
            Toast.makeText(requireContext(), "꽃을 먼저 선택 후 일기를 작성하시기를 바랍니다.", Toast.LENGTH_SHORT)
                .show()

            findNavController().navigate(R.id.selectFlowerDialog)
        }
    }

    // 광고 로드
    private fun setAd() {
        // TODO: 출시 전 광고 아이디 수정
        val adLoader = AdLoader
            .Builder(requireContext(), resources.getString(R.string.ad_native_unit_id_test))
            .forNativeAd {
                mViewModel.nativeAd.value = it
            }
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    // 하루 보상 확인
    private fun checkReward() {
        val prefKey = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
            .format(System.currentTimeMillis())

        if (mViewModel.getPref().getInt(prefKey, -1) == 1) { // 일기 보상 받는게 가능한 경우
            with(mViewModel.getPref().edit()) { // 하루 한번만 받을 수 있도록 설정
                putInt(prefKey, 0)
                commit()
            }

            findNavController().navigate(R.id.action_diaryListFragment_to_flowerStateDialog)
        }
    }
}