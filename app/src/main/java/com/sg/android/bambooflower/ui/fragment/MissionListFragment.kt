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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.MissionAdapter
import com.sg.android.bambooflower.data.HomeData
import com.sg.android.bambooflower.data.Mission
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.databinding.FragmentMissionListBinding
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.missionListFrag.MissionListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

// TODO:
//  . 광고 릴리스 키로 바꾸기
@AndroidEntryPoint
class MissionListFragment : Fragment(), MissionAdapter.MissionItemListener, View.OnClickListener {
    private val mViewModel by viewModels<MissionListViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    private lateinit var missionsAdapter: MissionAdapter
    private lateinit var user: User
    private var isChange = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentMissionListBinding.inflate(inflater)
        missionsAdapter = MissionAdapter().apply {
            setOnItemListener(this@MissionListFragment)
        }

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.gviewmodel = gViewModel
            this.clickListener = this@MissionListFragment

            this.missionList.adapter = missionsAdapter

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
        with((activity as MainActivity)) {
            supportActionBar?.title = "미션"
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            showToolbar()
        }
    }

    // 버튼 액션
    override fun onClick(v: View) {
        when (v.id) {
            R.id.mission_layout -> {
                gViewModel.mission.value = mViewModel.myMission.value
                findNavController().navigate(R.id.missionDialog)
            }
            R.id.change_btn -> {
                changeMission()
            }
            else -> {
            }
        }
    }

    // 미션 아이템 클릭
    override fun itemClickListener(pos: Int) {
        gViewModel.mission.value = missionsAdapter.getItem(pos)
        findNavController().navigate(R.id.missionDialog)
    }

    // 옵저버 설정
    private fun setObserver() {
        // 로딩 여부
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading && !isChange) {
                (requireActivity() as MainActivity).unEnableBottomView()
                getHomeData() // 홈 데이터를 가져옴
            } else {
                (requireActivity() as MainActivity).enableBottomView()
                missionsAdapter.submitData(gViewModel.missionList.value!!, user) // 미션 리스트 갱신
            }
        }
        gViewModel.syncData.observe(viewLifecycleOwner) {
            if (it) {
                mViewModel.isLoading.value = true
                gViewModel.syncData.value = false
            }
        }
        // 광고 로드 확인
        mViewModel.interstitialAd.observe(viewLifecycleOwner) {
            if (it != null) { // 로드 되었을 때
                it.show(requireActivity())
                it.fullScreenContentCallback = fullscreenCallback

                mViewModel.interstitialAd.value = null // 초기화
            }
        }
        mViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                Toast.makeText(requireContext(), "서버와 연결 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT)
                    .show()
                mViewModel.isLoading.value = false
            }
        }
    }

    // 홈 데이터 가져오기
    private fun getHomeData() {
        mViewModel.getHomeData(gViewModel.missionList.value == null)
            .addOnSuccessListener { result ->
                val jsonObject = JSONObject(result.data as MutableMap<Any?, Any?>).toString()
                val homeData = Gson().fromJson(jsonObject, HomeData::class.java)

                user = homeData.user
                gViewModel.user.value = user // 유저 정보
                if (homeData.missions.isNotEmpty()) { // 유저가 수행할 수 있는 미션리스트
                    gViewModel.missionList.value = homeData.missions
                    gViewModel.userImage.value = user.profileImage

                    if (user.isLevelUp) { // 레벨업 하였을 경우
                        findNavController().navigate(R.id.levelUpDialog)
                    }
                }

                val index = gViewModel.missionList // 유저가 수행중인 미션의 인덱스를 가져옴
                    .value!!
                    .indexOf(Mission(document = user.missionDoc!!))
                mViewModel.myMission.value = gViewModel.missionList.value?.get(index) // 미션 저장

                mViewModel.isLoading.value = false // 로딩 끝
                Log.i("Check", "동작 완료")
            }
            .addOnFailureListener {
                mViewModel.isError.value = true
            }
    }

    // 미션 바꾸기
    private fun changeMission() {
        with(MaterialAlertDialogBuilder(requireContext())) {
            setMessage("광고를 보고 미션을 바꾸겠습니까?")
            setPositiveButton("확인") { dialog, which ->
                loadAdAndChange() // 광고 로드
                (requireActivity() as MainActivity).loading()
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            show()
        }
    }

    // 광고 로드 콜백
    private val adCallBack = object : InterstitialAdLoadCallback() {
        override fun onAdLoaded(interstitialAd: InterstitialAd) {
            super.onAdLoaded(interstitialAd)

            // 로드 됐을 때
            mViewModel.interstitialAd.value = interstitialAd
            (requireActivity() as MainActivity).ready()
        }
    }

    // 광고 로드함
    private fun loadAdAndChange() {
        InterstitialAd.load(
            requireContext(),
            resources.getString(R.string.ad_full_unit_id_test),
            AdRequest.Builder().build(),
            adCallBack
        )
    }

    // 광고 사용자 동작 콜백
    private val fullscreenCallback = object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()

            // 광고를 본 후
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    isChange = true

                    // 미션을 바꿈
                    mViewModel.changeMission(user, gViewModel.missionList.value!!)
                    gViewModel.user.postValue(user)

                    val index = gViewModel.missionList // 유저가 수행중인 미션의 인덱스를 가져옴
                        .value!!
                        .indexOf(Mission(document = user.missionDoc!!))
                    mViewModel.myMission.postValue(gViewModel.missionList.value?.get(index)) // 미션 저장

                    isChange = false
                } catch (e: Exception) {
                    mViewModel.isError.postValue(true)
                }
            }
        }
    }
}