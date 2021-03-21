package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.PostAdapter
import com.sg.android.bambooflower.adapter.PostPagingAdapter
import com.sg.android.bambooflower.data.HomeData
import com.sg.android.bambooflower.databinding.FragmentHomeBinding
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.homeFragment.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

// TODO: 게시판 기능 구현 ( 전체 게시글 보기, 게시글 올리기, 좋아요 버튼, 조회수 )
//  1. 게시글 데이터 모델링 O
//  2. 게시글 리사이클러뷰 아이템 구현 O
//  3. 전체 게시글 보는 기능 구현 O
//  4. 게시글 작성 기능 구현 O
//  5. 홈에서 게시글 3개만 보여주게 구현 O
@AndroidEntryPoint
class HomeFragment : Fragment(), PostPagingAdapter.PostItemListener {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<HomeViewModel>()

    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        postAdapter = PostAdapter().apply {
            setOnPostListener(this@HomeFragment)
        }
        val binding = FragmentHomeBinding.inflate(inflater)

        with(binding) {
            viewmodel = mViewModel
            gviewmodel = gViewModel
            navController = findNavController()

            postList.adapter = postAdapter

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
    }
}