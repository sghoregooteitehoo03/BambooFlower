package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.sg.android.bambooflower.adapter.RankingPagingAdapter
import com.sg.android.bambooflower.databinding.FragmentRankingBinding
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.rankingFragment.RankingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RankingFragment : Fragment() {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<RankingViewModel>()

    private lateinit var rankingAdapter: RankingPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentRankingBinding.inflate(inflater)
        rankingAdapter = RankingPagingAdapter(gViewModel.user.value?.uid)

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.rankingList.adapter = rankingAdapter

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    override fun onStart() {
        super.onStart()
        // 툴바 설정
        with((activity as MainActivity)) {
            supportActionBar?.title = "랭킹"
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            showToolbar()
        }
    }

    private fun setObserver() {
        lifecycleScope.launch {
            mViewModel.getRankingList() // 랭킹 리스트를 가져옴
                .collect { pagingData ->
                    rankingAdapter.submitData(pagingData)
                }
        }
        lifecycleScope.launch {
            rankingAdapter.loadStateFlow.collectLatest {
                mViewModel.isLoading.value = it.refresh is LoadState.Loading // 로딩 표시여부
            }
        }
    }
}