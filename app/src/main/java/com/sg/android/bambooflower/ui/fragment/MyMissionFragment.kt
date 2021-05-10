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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sg.android.bambooflower.adapter.MissionPagingAdapter
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.databinding.FragmentMyMissionBinding
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.myMissionFragment.MyMissionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyMissionFragment : Fragment() {
    private val mViewModel by viewModels<MyMissionViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    private lateinit var missionAdapter: MissionPagingAdapter
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentMyMissionBinding.inflate(inflater)
        user = gViewModel.user.value!!
        missionAdapter = MissionPagingAdapter(user.uid!!)

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel

            with(missionList) { // 리스트 설정
                adapter = missionAdapter
                addItemDecoration(
                    DividerItemDecoration(
                        requireContext(),
                        LinearLayoutManager.VERTICAL
                    )
                )
            }

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
        // 툴바 설정
        with((activity as MainActivity).supportActionBar) {
            this?.title = "수행한 미션"
            this?.show()
            this?.setDisplayHomeAsUpEnabled(true)
        }
    }

    // 메뉴 설정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> false
        }
    }

    private fun setObserver() {
        mViewModel.isLoading.observe(viewLifecycleOwner) {
            if (!it) {
                mViewModel.size.value = missionAdapter.itemCount
            }
        }
        lifecycleScope.launch {
            // 내가 수행한 미션을 가져옴
            mViewModel.getMyMission(user.uid!!).collect { pagingData ->
                missionAdapter.submitData(pagingData)
            }
        }
        lifecycleScope.launch {
            missionAdapter.loadStateFlow.collectLatest {
                mViewModel.isLoading.value = it.refresh is LoadState.Loading // 로딩 표시여부
            }
        }
    }
}