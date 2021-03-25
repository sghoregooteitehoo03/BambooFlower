package com.sg.android.bambooflower.viewmodel.rankingFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(private val repository: RankingRepository) :
    ViewModel() {

    fun getRankingList() =
        repository.getRankingList()
            .flow
            .cachedIn(viewModelScope)
}