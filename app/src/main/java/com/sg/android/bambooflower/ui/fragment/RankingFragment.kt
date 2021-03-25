package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.RankingPagingAdapter
import com.sg.android.bambooflower.databinding.FragmentRankingBinding
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.rankingFragment.RankingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// TODO:
//  1. 레이아웃 구현 O
//  2. 리스트 아이템 구현 O
//  3. 페이징 구현 O
//  4. 아답터 구현 O

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
        rankingAdapter = RankingPagingAdapter(gViewModel.user.value?.uid)
        val binding = FragmentRankingBinding.inflate(inflater)

        with(binding) {
            viewmodel = mViewModel
            rankingList.adapter = rankingAdapter

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    private fun setObserver() {
        lifecycleScope.launch {
            mViewModel.getRankingList()
                .collect { pagingData ->
                    rankingAdapter.submitData(pagingData)
                }
        }
    }
}