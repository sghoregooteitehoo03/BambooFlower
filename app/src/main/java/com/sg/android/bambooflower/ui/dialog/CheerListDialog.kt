package com.sg.android.bambooflower.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.CheerPagingAdapter
import com.sg.android.bambooflower.databinding.DialogCheerListBinding
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.cheerListDialog.CheerListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheerListDialog : BottomSheetDialogFragment(), View.OnClickListener {
    private val mViewModel by viewModels<CheerListViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    private lateinit var cheerAdapter: CheerPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = DialogCheerListBinding.inflate(inflater)
        cheerAdapter = CheerPagingAdapter()

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.clickListener = this@CheerListDialog
            this.acceptList.adapter = cheerAdapter

            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)

        setObserver()
        cheerAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh !is LoadState.Loading
                && mViewModel.cheerList.value != null
            ) {
                mViewModel.isLoading.value = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gViewModel.post.value = null
    }

    // 버튼 액션
    override fun onClick(v: View) {
        when (v.id) {
            R.id.close_btn -> {
                findNavController().navigateUp()
            }
        }
    }

    private fun setObserver() {
        // 로딩 여부
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                mViewModel.getCheerList(gViewModel.post.value!!.id)
            } else {
                mViewModel.size.value = cheerAdapter.itemCount
            }
        }
        // 리스트
        mViewModel.cheerList.observe(viewLifecycleOwner) {
            if (it != null) {
                lifecycleScope.launch {
                    it.collect { paging ->
                        cheerAdapter.submitData(paging)
                    }
                }
            }
        }
    }
}