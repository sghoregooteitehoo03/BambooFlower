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
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.data.HomeData
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.databinding.FragmentHomeBinding
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.homeFrag.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

// TODO:
//  . VIEW 표시 O
//  . 포기하기 O
//  . 물 주기 (나중에)
//  . 꽃 선택 O
//  . 퀘스트, 하루일기, 정원 이동 (나중에)

@AndroidEntryPoint
class HomeFragment : Fragment(), View.OnClickListener {
    private val mViewModel by viewModels<HomeViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    private lateinit var user: User

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

        mViewModel.checkFirst()
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
            R.id.flower_layout -> { // 꽃 클릭 시
                val flower = gViewModel.flower.value!!
                if (flower.state == 0) { // 선택한 꽃이 존재하지 경우
                    findNavController().navigate(R.id.selectFlowerDialog)
                }
            }
            R.id.cancel_button -> { // 포기
                giveUpFlower()
            }
            R.id.quest_view -> { // 퀘스트 목록으로 이동
                findNavController().navigate(R.id.action_homeFragment_to_questListFragment)
            }
            R.id.diary_view -> {

            }
            R.id.garden_view -> {

            }
        }
    }

    private fun setObserver() {
        gViewModel.user.observe(viewLifecycleOwner) {
            if (it != null) {
                user = it
            } else { // 유저 데이터가 null일 때만 데이터를 가져옴
                mViewModel.isLoading.value = true
            }
        }
        // 꽃 데이터 수정
        mViewModel.updateFlower.observe(viewLifecycleOwner) { flower ->
            if (flower != null) {
                // TODO: 나중에 광고 넣기
                gViewModel.user.value = user
                gViewModel.flower.value = flower

                mViewModel.updateFlower.value = null // 초기화
            }
        }
        // 로딩 여부
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                // 바텀뷰 클릭 비활성화
                (requireActivity() as MainActivity).unEnableBottomView()
                getHomeData()
            } else {
                // 바텀뷰 클릭 활성화
                (requireActivity() as MainActivity).enableBottomView()
            }
        }
        // 메인 로딩
        mViewModel.mainLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                (requireActivity() as MainActivity).loading()
            } else {
                (requireActivity() as MainActivity).ready()
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

    private fun getHomeData() {
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

    // 꽃을 포기함
    private fun giveUpFlower() {
        with(MaterialAlertDialogBuilder(requireContext())) {
            val flower = gViewModel.flower.value!!
            setMessage("\"${flower.name}\" 을(를) 키우는 것을 포기하시겠습니까?")
            setPositiveButton("확인") { dialog, which ->
                mViewModel.giveUpFlower(user)
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            show()
        }
    }
}