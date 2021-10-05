package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.ShopAdapter
import com.sg.android.bambooflower.data.Shop
import com.sg.android.bambooflower.databinding.FragmentShopListBinding
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.shopListFrag.ShopListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShopListFragment(private val category: Int) :
    Fragment(), ShopAdapter.ShopItemListener {
    private val mViewModel by viewModels<ShopListViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    private lateinit var shopAdapter: ShopAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentShopListBinding.inflate(inflater)
        shopAdapter = ShopAdapter().apply {
            setOnShopItemListener(this@ShopListFragment)
        }

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.shopList.adapter = shopAdapter

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    // 상점 아잍메 클릭
    override fun onItemClickListener(pos: Int) {
        val shopData = shopAdapter.getItem(pos)
        if (!shopData.isExists) { // 구매하지 않은 상품일때만 동작
            if (shopData.price == 0) { // 광고를 통해 얻을 수 있는 상품일 경우
                showAd(shopData)
            } else {
                buyItem(shopData, pos)
            }
        }
    }

    private fun setObserver() {
        // 상점 데이터
        mViewModel.shopList.observe(viewLifecycleOwner) { list ->
            if (list != null) {
                shopAdapter.syncData(list)
            }
        }
        // 구매 성공여부
        mViewModel.isBuyComplete.observe(viewLifecycleOwner) { syncPos ->
            if (syncPos != -1) {
                // 데이터 갱신
                gViewModel.user.value = gViewModel.user.value
                gViewModel.isSyncProfile.value = true

                shopAdapter.notifyItemChanged(syncPos)
            }
        }
        // 로드 된 광고
        mViewModel.rewardAd.observe(viewLifecycleOwner) { rewardAd ->
            if (rewardAd != null) { // 광고가 로드 되었을 때
                // 광고 보여주기
                rewardAd.show(
                    requireActivity(),
                    OnUserEarnedRewardListener { rewardItem ->
                        // 광고를 다 시정하였을 때
                        val user = gViewModel.user.value!!
                        val pos = shopAdapter.getItemPosWithName("튤립")

                        mViewModel.buyItem(user, shopAdapter.getItem(pos), pos)
                    }
                )

                mViewModel.rewardAd.value = null // 광고 초기화
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
        // 로딩 여부
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                val uid = gViewModel.user.value!!.uid
                mViewModel.getShopData(uid, category)
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

    // 상점 아이템 구매
    private fun buyItem(shopData: Shop, itemPos: Int) {
        with(MaterialAlertDialogBuilder(requireContext())) {
            setMessage("\"${shopData.name}\" 을(를) 구매하시겠습니까?")
            setPositiveButton("확인") { dialog, which ->
                val user = gViewModel.user.value!!

                // 구매 가능할 경우
                if (user.money >= shopData.price) {
                    mViewModel.buyItem(user, shopData, itemPos)
                } else {
                    Toast.makeText(requireContext(), "포인트가 부족합니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.cancel()
            }

            show()
        }
    }

    // 리워드 광고 보여주기
    private fun showAd(shopData: Shop) {
        with(MaterialAlertDialogBuilder(requireContext())) {
            setMessage("광고를 보고 \"${shopData.name}\" 을(를) 얻으시겠습니까?")
            setPositiveButton("확인") { dialog, which ->
                mViewModel.mainLoading.value = true // 로딩 시작

                // 광고 로드
                RewardedAd.load(
                    requireContext(),
                    getString(R.string.ad_reward_unit_id_test),
                    AdRequest.Builder().build(),
                    adLoadCallback
                )
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            show()
        }
    }

    // 광고 로드콜백
    private val adLoadCallback = object : RewardedAdLoadCallback() {
        // 로드 성공
        override fun onAdLoaded(rewardedAd: RewardedAd) {
            super.onAdLoaded(rewardedAd)
            mViewModel.rewardAd.value = rewardedAd
            mViewModel.mainLoading.value = false // 로딩 끝
        }

        // 로드 실패
        override fun onAdFailedToLoad(error: LoadAdError) {
            super.onAdFailedToLoad(error)
            mViewModel.isError.value = true
            mViewModel.mainLoading.value = false // 로딩 끝
        }
    }
}