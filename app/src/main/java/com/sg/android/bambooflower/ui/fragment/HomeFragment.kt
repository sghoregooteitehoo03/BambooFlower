package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.data.HomeData
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.databinding.FragmentHomeBinding
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.homeFragment.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

// TODO: 게시판 기능 구현 ( 전체 게시글 보기, 게시글 올리기, 좋아요 버튼, 조회수 )
//  1. 게시글 데이터 모델링 O
//  2. 게시글 리사이클러뷰 아이템 구현 O
//  3. 전체 게시글 보는 기능 구현
//  4. 게시글 작성 기능 구현
//  5. 홈에서 게시글 3개만 보여주게 구현
//  6. 좋아요 및 조회수 기능 구현

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater)
        with(binding) {
            viewmodel = mViewModel
            gviewmodel = gViewModel

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.getHomeData()
            .addOnSuccessListener {
                val jsonObject = JSONObject(it.data as MutableMap<Any?, Any?>).toString()
                val homeData = Gson().fromJson(jsonObject, HomeData::class.java)
                Log.i("Check", "data: $homeData")

                gViewModel.setUser(homeData.user)
                mViewModel.setMission(homeData.user.myMission!!)
            }
    }
}