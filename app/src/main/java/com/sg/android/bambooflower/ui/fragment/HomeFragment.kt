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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.DiaryPagingAdapter
import com.sg.android.bambooflower.adapter.PostAdapter
import com.sg.android.bambooflower.adapter.PostPagingAdapter
import com.sg.android.bambooflower.data.HomeData
import com.sg.android.bambooflower.databinding.FragmentHomeBinding
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.homeFragment.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONObject

// TODO: 디자인 및 기능추가
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

    // 리사이클러뷰 아이템 클릭
    override fun onItemClickListener(pos: Int) {
        gViewModel.setPost(postAdapter.getItem(pos))
        findNavController().navigate(R.id.postDialog)
    }

    override fun addItemClickListener() {
        findNavController().navigate(R.id.diaryWriteFragment)
    }

    override fun onDiaryItemClickListener(pos: Int) {
        val diaryData = diaryAdapter.getDiaryItem(pos)
        gViewModel.diary.value = diaryData

        findNavController().navigate(R.id.diaryViewerFragment)
    }

    private fun setObserver() {
        gViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user == null) { // 홈 스크린에 표현할 데이터들을 가져옴
                mViewModel.getHomeData()
                    .addOnSuccessListener {
                        val jsonObject = JSONObject(it.data as MutableMap<Any?, Any?>).toString()
                        val homeData = Gson().fromJson(jsonObject, HomeData::class.java)
                        Log.i("Check", "data: $homeData")

                        gViewModel.setUser(homeData.user) // 유저를 공유할 수 있게 GlobalViewModel에 저장함

                        mViewModel.setMission(homeData.user.myMission!!)
                        mViewModel.setPosts(homeData.posts)
                    }
            }
        }

        mViewModel.posts.observe(viewLifecycleOwner) { // 최근 게시글을 가져옴
            postAdapter.syncData(it)
        }

        lifecycleScope.launch {
            mViewModel.diaries.collect { pagingData ->
                diaryAdapter.submitData(pagingData)
            }
        }
    }
}