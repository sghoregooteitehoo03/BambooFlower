package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.DiaryPagingAdapter
import com.sg.android.bambooflower.adapter.PostAdapter
import com.sg.android.bambooflower.adapter.PostPagingAdapter
import com.sg.android.bambooflower.data.HomeData
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.databinding.FragmentHomeBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.homeFragment.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject

// TODO: 게시글 클릭 시 팅기는 버그 수정 O
@AndroidEntryPoint
class HomeFragment : Fragment(), PostPagingAdapter.PostItemListener,
    DiaryPagingAdapter.DiaryItemListener, View.OnClickListener {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<HomeViewModel>()

    private lateinit var user: User
    private lateinit var postAdapter: PostAdapter
    private lateinit var diaryAdapter: DiaryPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentHomeBinding.inflate(inflater)
        postAdapter = PostAdapter().apply {
            setOnPostListener(this@HomeFragment)
        }
        diaryAdapter = DiaryPagingAdapter().apply {
            setOnDiaryItemListener(this@HomeFragment)
        }

        with(binding) {
            this.viewmodel = mViewModel
            this.gviewmodel = gViewModel
            this.clickListener = this@HomeFragment
            this.navController = findNavController()

            with(postList) {
                postAdapter.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT
                adapter = postAdapter
                isNestedScrollingEnabled = false
            }
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
        findNavController().navigate(R.id.postFragment)
    }

    // 일기 작성 클릭
    override fun addItemClickListener() {
        findNavController().navigate(R.id.action_homeFragment_to_diaryWriteFragment)
    }

    // 일기 아이템 클릭
    override fun onDiaryItemClickListener(pos: Int) {
        val diaryData = diaryAdapter.getDiaryItem(pos)
        gViewModel.diary.value = diaryData

        findNavController().navigate(R.id.diaryViewerFragment)
    }

    // 버튼 액션
    override fun onClick(v: View) {
        when (v.id) {
            R.id.success_button -> { // 수행완료
                successMission()
            }
            R.id.change_button -> { // 미션 바꾸기
                changeMission()
            }
            else -> {
            }
        }
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
        gViewModel.syncData.observe(viewLifecycleOwner) {
            if (it) {
                mViewModel.isLoading.value = true
                gViewModel.syncData.value = false
            }
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
                Log.i("Check", "data: ${homeData.user}")

                gViewModel.user.value = homeData.user // 유저를 공유할 수 있게 GlobalViewModel에 저장함
                user = homeData.user

                mViewModel.posts.value = homeData.posts
                mViewModel.currentTime.value = System.currentTimeMillis()

                mViewModel.isLoading.value = false
            }
    }

    // 미션 수행 버튼 눌렀을 때
    private fun successMission() {
        with(MaterialAlertDialogBuilder(requireContext())) {
            setMessage("인증게시판으로 이동합니다.")
            setPositiveButton("확인") { dialog, which ->
                findNavController().navigate(R.id.addPostFragment)
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            show()
        }
    }

    // 미션 바꾸기 버튼 눌렀을 때
    private fun changeMission() {
        with(MaterialAlertDialogBuilder(requireContext())) {
            setMessage("광고를 보고 미션을 바꾸겠습니까?")
            setPositiveButton("확인") { dialog, which ->
                CoroutineScope(Dispatchers.IO).launch {
                    mViewModel.changeMission(user)
                    gViewModel.user.postValue(user)
                }
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            show()
        }
    }
}