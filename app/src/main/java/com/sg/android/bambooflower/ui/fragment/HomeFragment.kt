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

// TODO:
//  1. 미션 보여주는 뷰 구현 O
//  2. Firestore db 구조 설정 O
//  3. Firestore에 미션 저장 O
//  4. 간단한 테스트 O
//  5. Firestore에서 읽어와 뷰에 바인딩 O
//  6. 하루하루 미션 바뀌는거 구현 O
//  7. 수행완료 구현 O
//  8. 미션 바꾸기 구현 O

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