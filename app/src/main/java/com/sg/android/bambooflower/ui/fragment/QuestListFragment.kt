package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.QuestPagingAdapter
import com.sg.android.bambooflower.adapter.UsersQuestAdapter
import com.sg.android.bambooflower.data.UsersQuest
import com.sg.android.bambooflower.databinding.FragmentQuestListBinding
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.questListFrag.QuestListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// TODO:
//  . 아이콘 수정(나중에)
//  . 리스트 로딩 애니메이션 구현(나중에)

@AndroidEntryPoint
class QuestListFragment : Fragment(), UsersQuestAdapter.UsersQuestItemListener,
    QuestPagingAdapter.QuestItemListener {

    private val mViewModel by viewModels<QuestListViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    private lateinit var usersQuestAdapter: UsersQuestAdapter
    private lateinit var questAdapter: QuestPagingAdapter
    private lateinit var scrollView: NestedScrollView

    private var isMove = false // 옵저버 동작을 막기위한 변수

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentQuestListBinding.inflate(inflater)
        usersQuestAdapter = UsersQuestAdapter().apply {
            setOnItemListener(this@QuestListFragment)
        }
        questAdapter = QuestPagingAdapter().apply {
            setOnItemListener(this@QuestListFragment)
        }
        scrollView = binding.mainLayout

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.myQuestList.adapter = usersQuestAdapter
            this.questList.adapter = questAdapter

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
        with((activity as MainActivity)) {
            supportActionBar?.title = "퀘스트"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            showToolbar()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        gViewModel.usersQuest.value = null
        gViewModel.usersQuestList.value = null
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

    // 유저가 수행중인 퀘스트를 눌렀을 때
    override fun usersQuestClickListener(pos: Int) {
        val data = usersQuestAdapter.getItem(pos)

        if (data != null) {
            isMove = true // 옵저버 동작을 막음
            gViewModel.usersQuest.value = data
            gViewModel.usersQuestList.value = mViewModel.usersQuestList.value?.toMutableList()

            findNavController().navigate(R.id.myQuestDialog)
        }
    }

    // 퀘스트 목록에 퀘스트를 눌렀을 때
    override fun onQuestClickListener(pos: Int) {
        val data = UsersQuest(
            0,
            questAdapter.getItemData(pos),
            0,
            0
        )

        // 유저가 수행하고 있지 않은 퀘스트일 경우
        if (!questAdapter.isQuestPerform(data.quest.id)) {
            isMove = true // 옵저버 동작을 막음
            gViewModel.usersQuest.value = data
            gViewModel.usersQuestList.value = mViewModel.usersQuestList.value?.toMutableList()

            findNavController().navigate(R.id.questDialog)
        } else { // 유저가 수행중인 퀘스트일 경우
            scrollView.smoothScrollTo(0, 0)
        }
    }

    // 옵저버 설정
    private fun setObserver() {
        gViewModel.usersQuestList.observe(viewLifecycleOwner) {
            if (!isMove && it != null) {
                mViewModel.usersQuestList.value = it

                // 초기화
                gViewModel.usersQuest.value = null
                gViewModel.usersQuestList.value = null
            }

            isMove = false
        }

        // 유저 퀘스트
        mViewModel.usersQuestList.observe(viewLifecycleOwner) { list ->
            if (list != null) {
                usersQuestAdapter.syncData(list)
                questAdapter.syncData(list.map { it.quest.id })
            }
        }
        // 로딩 여부
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                val user = gViewModel.user.value!!
                mViewModel.getUsersQuest(user.uid)
            }
        }
        // 오류 여부
        mViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                Toast.makeText(requireContext(), ErrorMessage.CONNECT_ERROR, Toast.LENGTH_SHORT)
                    .show()
                mViewModel.isError.value = false
            }
        }
        lifecycleScope.launch {
            // 모든 퀘스트 목록
            mViewModel.questList.collect {
                questAdapter.submitData(it)
            }
        }
    }
}