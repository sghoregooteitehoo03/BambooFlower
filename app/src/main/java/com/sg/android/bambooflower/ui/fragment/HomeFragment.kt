package com.sg.android.bambooflower.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
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
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named

// TODO:
//  . 제공할 미션 없을 때 처리 O
//  . 광고 릴리스 키로 바꾸기
@AndroidEntryPoint
class HomeFragment : Fragment(), PostPagingAdapter.PostItemListener,
    DiaryPagingAdapter.DiaryItemListener, View.OnClickListener {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<HomeViewModel>()

    @Inject
    @Named(Contents.PREF_CHECK_FIRST)
    lateinit var checkPref: SharedPreferences
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
            this.blank = ""

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
        checkFirst()
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
        (activity as MainActivity).hideSatisfaction()
    }

    // 리사이클러뷰 아이템 클릭
    override fun onItemClickListener(pos: Int) {
        gViewModel.post.value = postAdapter.getItem(pos)
        findNavController().navigate(R.id.action_homeFragment_to_postFragment)
    }

    // 일기 작성 클릭
    override fun addItemClickListener() {
        findNavController().navigate(R.id.action_homeFragment_to_diaryWriteFragment)
    }

    // 일기 아이템 클릭
    override fun onDiaryItemClickListener(pos: Int) {
        val diaryData = diaryAdapter.getDiaryItem(pos)
        gViewModel.diary.value = diaryData

        findNavController().navigate(R.id.action_homeFragment_to_diaryViewerFragment)
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
            R.id.more_text -> { // 더보기
                findNavController().navigate(R.id.action_homeFragment_to_postListFragment)
            }
            else -> {
            }
        }
    }

    private fun setObserver() {
        mViewModel.posts.observe(viewLifecycleOwner) { // 최근 게시글을 가져옴
            postAdapter.syncData(it)
        }
        gViewModel.syncData.observe(viewLifecycleOwner) {
            if (it) {
                mViewModel.isLoading.value = true
                gViewModel.syncData.value = false
            }
        }
        mViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                Toast.makeText(requireContext(), "서버와 연결 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT)
                    .show()
                mViewModel.isLoading.value = false
            }
        }
        mViewModel.isLoading.observe(viewLifecycleOwner) { // 데이터 갱신
            if (it) {
                (activity as MainActivity).hideBottomView()
                getHomeData()
            } else {
                (activity as MainActivity).showBottomView()
            }
        }
        mViewModel.interstitialAd.observe(viewLifecycleOwner) { // 광고 로드 확인
            if (it != null) { // 로드 되었을 때
                it.show(requireActivity())
                it.fullScreenContentCallback = fullscreenCallback

                mViewModel.interstitialAd.value = null // 초기화
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
            .addOnFailureListener {
                mViewModel.isError.value = true
            }
    }

    // 미션 수행 버튼 눌렀을 때
    private fun successMission() {
        with(MaterialAlertDialogBuilder(requireContext())) {
            setMessage("인증게시판으로 이동합니다.")
            setPositiveButton("확인") { dialog, which ->
                findNavController().navigate(R.id.action_homeFragment_to_addPostFragment)
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
                loadAdAndChange() // 광고 로드
                (requireActivity() as MainActivity).loading()
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            show()
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

    // 광고 로드 콜백
    private val adCallBack = object : InterstitialAdLoadCallback() {
        override fun onAdLoaded(interstitialAd: InterstitialAd) {
            super.onAdLoaded(interstitialAd)

            // 로드 됐을 때
            mViewModel.interstitialAd.value = interstitialAd
            (requireActivity() as MainActivity).ready()
        }
    }

    // 광고 사용자 동작 콜백
    private val fullscreenCallback = object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()

            // 광고를 본 후
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    mViewModel.changeMission(user)
                    gViewModel.user.postValue(user)
                } catch (e: Exception) {
                    mViewModel.isError.value = true
                }
            }
        }
    }

    private fun checkFirst() {
        if (checkPref.getBoolean(Contents.PREF_KEY_IS_FIRST, true)) {
            with(checkPref.edit()) {
                putBoolean(Contents.PREF_KEY_IS_FIRST, false)
                commit()
            }
        }
    }
}