package com.sg.android.bambooflower.ui.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.DiaryPagingAdapter
import com.sg.android.bambooflower.adapter.PostAdapter
import com.sg.android.bambooflower.adapter.PostPagingAdapter
import com.sg.android.bambooflower.data.HomeData
import com.sg.android.bambooflower.databinding.FragmentHomeBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.homeFragment.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONObject

// TODO:
//  1. 디자인 O
//  2. 데이터 읽어오는 화면 디자인 O
//  3. 데이터 갱신 O
//  4. 수락 시 디자인 O
//  5. 변경 시 디지인 O
//  6. 감사일기 각 계정마다 존재하게 구현 O
//  7. 달력 누를 시 일 별로 작성한 일기장 보여주는 기능 구현 O
@AndroidEntryPoint
class HomeFragment : Fragment(), PostPagingAdapter.PostItemListener,
    DiaryPagingAdapter.DiaryItemListener {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<HomeViewModel>()

    private lateinit var postAdapter: PostAdapter
    private lateinit var diaryAdapter: DiaryPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        postAdapter = PostAdapter().apply {
            setOnPostListener(this@HomeFragment)
        }
        diaryAdapter = DiaryPagingAdapter().apply {
            setOnDiaryItemListener(this@HomeFragment)
        }
        val binding = FragmentHomeBinding.inflate(inflater)

        with(binding) {
            viewmodel = mViewModel
            gviewmodel = gViewModel
            navController = findNavController()

            postList.adapter = postAdapter
            diaryList.adapter = diaryAdapter

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
        // 툴바설정
        with((activity as MainActivity).supportActionBar) {
            this?.title = "홈"
            this?.setDisplayHomeAsUpEnabled(false)
            this?.show()
        }
    }

    // 리사이클러뷰 아이템 클릭
    override fun onItemClickListener(pos: Int) {
        gViewModel.post.value = postAdapter.getItem(pos)
        findNavController().navigate(R.id.postDialog)
    }

    // 일기 작성 아이템 클릭
    override fun addItemClickListener() {
        findNavController().navigate(R.id.diaryWriteFragment)
    }

    // 일기 아이템 클릭
    override fun onDiaryItemClickListener(pos: Int) {
        val diaryData = diaryAdapter.getDiaryItem(pos)
        gViewModel.diary.value = diaryData

        findNavController().navigate(R.id.diaryViewerFragment)
    }

    private fun setObserver() {
        mViewModel.posts.observe(viewLifecycleOwner) { // 최근 게시글을 가져옴
            postAdapter.syncData(it)
        }
        mViewModel.isLoading.observe(viewLifecycleOwner) { // 데이터 갱신
            if (it) {
                getHomeData()
            }
        }
        mViewModel.buttonAction.observe(viewLifecycleOwner) { action -> // 버튼 액션
            if (action.isNotEmpty()) {
                when (action) {
                    Contents.ACTION_COMPLETE_MISSION -> {
                        successMission()
                    }
                    Contents.ACTION_CHANGE_MISSION -> {
                        changeMission()
                    }
                    else -> {
                    }
                }

                mViewModel.setButtonAction("")
            }
        }
        mViewModel.isAchieved.observe(viewLifecycleOwner) { // 미션 수행 여부
            mViewModel.buttonEnabled.value = !it
        }

        lifecycleScope.launch {
            mViewModel.diaries.collect { pagingData ->
                diaryAdapter.submitData(pagingData)
            }
        }
    }

    // 홈 화면에 표시할 데이터를 가져 옴
    private fun getHomeData() {
        mViewModel.getHomeData()
            .addOnSuccessListener {
                val jsonObject = JSONObject(it.data as MutableMap<Any?, Any?>).toString()
                val homeData = Gson().fromJson(jsonObject, HomeData::class.java)
                Log.i("Check", "data: ${homeData.user.isAchieved}")

                gViewModel.user.value = homeData.user // 유저를 공유할 수 있게 GlobalViewModel에 저장함

                mViewModel.mission.value = homeData.user.myMission!!
                mViewModel.posts.value = homeData.posts

                mViewModel.isLoading.value = false
                mViewModel.currentTime.value = System.currentTimeMillis()
                mViewModel.isAchieved.value = homeData.user.isAchieved!!
            }
    }

    // 미션 수행 버튼 눌렀을 때
    private fun successMission() {
        with(AlertDialog.Builder(requireContext())) {
            setMessage("인증게시판으로 이동합니다.")
            setPositiveButton("확인") { dialog, which ->
                mViewModel.successMission(gViewModel.user.value!!)
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            show()
        }
    }

    // 미션 바꾸기 버튼 눌렀을 때
    private fun changeMission() {
        with(AlertDialog.Builder(requireContext())) {
            setMessage("광고를 보고 미션을 바꾸겠습니까?")
            setPositiveButton("확인") { dialog, which ->
                mViewModel.changeMission(gViewModel.user.value!!)
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            show()
        }
    }
}