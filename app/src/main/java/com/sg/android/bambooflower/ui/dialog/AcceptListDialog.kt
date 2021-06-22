package com.sg.android.bambooflower.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sg.android.bambooflower.adapter.AcceptPagingAdapter
import com.sg.android.bambooflower.databinding.DialogAcceptListBinding
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.acceptListDialog.AcceptListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AcceptListDialog : BottomSheetDialogFragment() {
    private val mViewModel by viewModels<AcceptListViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    private lateinit var acceptAdapter: AcceptPagingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // 인스턴스 설정
        val binding = DialogAcceptListBinding.inflate(inflater)
        acceptAdapter = AcceptPagingAdapter()

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.acceptList.adapter = acceptAdapter

            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    private fun setObserver() {
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (!isLoading) {
                mViewModel.size.value = acceptAdapter.itemCount
            }
        }
        lifecycleScope.launch {
            mViewModel.getAcceptList(gViewModel.post.value?.postPath!!)
                .collect { pagingData ->
                    acceptAdapter.submitData(pagingData)
                }
        }
        lifecycleScope.launch {
            acceptAdapter.loadStateFlow.collectLatest {
                mViewModel.isLoading.value = it.refresh is LoadState.Loading // 로딩 표시여부
            }
        }
    }
}