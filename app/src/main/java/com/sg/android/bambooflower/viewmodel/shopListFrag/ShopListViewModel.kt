package com.sg.android.bambooflower.viewmodel.shopListFrag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.gson.Gson
import com.sg.android.bambooflower.data.Shop
import com.sg.android.bambooflower.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ShopListViewModel @Inject constructor(
    private val repository: ShopListRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData(true) // 로딩 여부
    private val _shopList = MutableLiveData<List<Shop>>() // 상점 리스트

    val isLoading: LiveData<Boolean> = _isLoading
    val shopList: LiveData<List<Shop>> = _shopList

    val rewardAd = MutableLiveData<RewardedAd?>(null) // 로드 된 광고
    val mainLoading = MutableLiveData(false) // 메인 로딩
    val isError = MutableLiveData(false) // 오류 여부
    val isBuyComplete = MutableLiveData(-1) // 구매 성공 여부

    // 상점 데이터를 가져옴
    fun getShopData(uid: String, category: Int) = viewModelScope.launch {
        try {
            val result = repository.getShopData(uid, category)
                .data as Map<*, *>
            if (result["complete"] == null) { // 오류 확인
                throw NullPointerException()
            }

            // 상점 리스트 받음
            _shopList.value = (result["shopList"] as List<*>).map { shop ->
                val jsonObject = JSONObject(shop as Map<*, *>).toString()
                Gson().fromJson(jsonObject, Shop::class.java)
            }
        } catch (e: Exception) {
            isError.value = true
            e.printStackTrace()
        }

        _isLoading.value = false // 로딩 끝
    }

    // 상점 아이템 구매
    fun buyItem(user: User, shop: Shop, itemPos: Int) = viewModelScope.launch {
        mainLoading.value = true // 로딩 시작

        try {
            val result = repository.buyItem(user.uid, shop.id)
                .data as Map<*, *>
            if (result["complete"] == null) { // 오류 확인
                throw NullPointerException()
            }

            user.money -= shop.price
            shop.isExists = true
            isBuyComplete.value = itemPos // 구매 성공
        } catch (e: Exception) {
            isError.value = true
            e.printStackTrace()
        }

        mainLoading.value = false // 로딩 여부
    }
}