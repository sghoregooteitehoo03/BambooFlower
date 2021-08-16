package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.data.HomeData
import com.sg.android.bambooflower.databinding.FragmentHomeBinding
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.homeFrag.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

// TODO:
//  . VIEW 표시 O
//  . 포기하기 (나중에)
//  . 물 주기 (나중에)
//  . 꽃 선택 (나중에)
//  . 퀘스트, 하루일기, 정원 이동 (나중에)

@AndroidEntryPoint
class HomeFragment : Fragment(), View.OnClickListener {
    private val mViewModel by viewModels<HomeViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentHomeBinding.inflate(inflater)

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.gviewmodel = gViewModel
            this.clickListener = this@HomeFragment

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
        // 툴바 설정
        with((activity as MainActivity)) {
            supportActionBar?.title = ""
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            showToolbar()
        }
    }

    // 버튼 액션
    override fun onClick(v: View) {
        when (v.id) {
            R.id.flower_layout -> {

            }
            R.id.quest_view -> {

            }
            R.id.diary_view -> {

            }
            R.id.garden_view -> {

            }
        }
    }

    private fun setObserver() {
        gViewModel.user.observe(viewLifecycleOwner) {
            mViewModel.isLoading.value = it == null // 유저 데이터가 null일 때만 데이터를 가져옴
        }
        // 로딩 여부
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                mViewModel.getHomeData() // 기본 데이터를 가져옴
                    .addOnSuccessListener { result ->
                        val jsonObject = JSONObject(result.data as MutableMap<*, *>).toString()
                        val homeData = Gson().fromJson(jsonObject, HomeData::class.java)

                        if (homeData.user != null && homeData.flower != null) {
                            gViewModel.user.value = homeData.user
                            gViewModel.flower.value = homeData.flower

                            mViewModel.isLoading.value = false // 로딩 끝
                            Log.i("getHomeData", "동작")
                        } else {
                            mViewModel.isError.value = true // 오류발생
                        }
                    }
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
    }
}