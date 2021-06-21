package com.sg.android.bambooflower.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.gms.ads.MobileAds
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.databinding.ActivityMainBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

// TODO:
//  . 미션 보여주는 뷰 디자인 수정 O
//  . 서버 점검할 때 앱 못들어오게 O
//  . 인증방식 변경 O
//  . 당일 수행한 미션 삭제 시 다시 수행할 수 있게 구현 O
//  . 바텀 다이얼로그 디자인 수정

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val gViewModel by viewModels<GlobalViewModel>()

    @Inject
    @Named(Contents.PREF_CHECK_FIRST)
    lateinit var checkPref: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private lateinit var imm: InputMethodManager
    private var backAvailable = true

    private val FINISH_INTERVAL_TIME = 2000L
    private var backPressedTime = 0L
    private lateinit var backToast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 인스턴스 설정
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        imm =
            applicationContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        // 툴바 설정
        setSupportActionBar(binding.mainToolbar)

        val navFrag = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navFrag.navController

        binding.bottomNavView.setupWithNavController(navController)
        binding.inputSearch.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    gViewModel.searchValue.value = binding.inputSearch.text.toString()
                    true
                }
                else -> false
            }
        }

        // Ad Init
        MobileAds.initialize(this)

        if (intent.getBooleanExtra(Contents.EXTRA_IS_LOGIN, false)) {
            // 로그인 되어있으면 홈 화면으로 넘어감
            navController.navigate(R.id.action_loginFragment_to_homeFragment)
            intent.putExtra(Contents.EXTRA_IS_LOGIN, false)
        } else if (checkPref.getBoolean(Contents.PREF_KEY_IS_FIRST, true)) {
            // 처음 앱을 킨 유저일 시 온보딩 화면으로 이동
            navController.navigate(R.id.action_global_onboardFragment)
        }
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.homeFragment, R.id.rankingFragment, R.id.profileFragment -> {
                    showBottomView()
                }
                else -> {
                    hideBottomView()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (backAvailable) {
            if (binding.bottomNavView.visibility == View.VISIBLE) {
                val tempTime = System.currentTimeMillis()
                val intervalTime = tempTime - backPressedTime

                if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                    finish()
                    backToast.cancel()
                } else {
                    backPressedTime = tempTime
                    backToast = Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT)
                        .apply {
                            show()
                        }
                }
            } else {
                super.onBackPressed()
            }
        }
    }

    // 로딩 화면 표시
    fun loading() {
        backAvailable = false
        binding.loadingView.setVisible(true, window)
    }

    // 로딩 화면 없애기
    fun ready() {
        backAvailable = true
        binding.loadingView.setVisible(false, window)
    }

    fun showSatisfaction(image: Bitmap) {
        with(binding.satisfactionImage) {
            visibility = View.VISIBLE
            Glide.with(context).load(image)
                .into(this)
        }
    }

    fun hideSatisfaction() {
        binding.satisfactionImage.visibility = View.GONE
    }

    fun showBottomView() {
        if (binding.bottomNavView.visibility == View.GONE) {
            with(binding.bottomNavView) {
                visibility = View.VISIBLE
                binding.bottomNavView.menu.forEach {
                    it.isEnabled = true
                }

                animate().setDuration(resources.getInteger(R.integer.transaction_duration).toLong())
                    .alpha(1f)
                    .translationY(0f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            visibility = View.VISIBLE
                        }
                    })
                    .withLayer()
            }
        }
    }

    fun hideBottomView() {
        if (binding.bottomNavView.visibility == View.VISIBLE) {
            with(binding.bottomNavView) {
                binding.bottomNavView.menu.forEach {
                    it.isEnabled = false
                }

                animate().setDuration(resources.getInteger(R.integer.transaction_duration).toLong())
                    .alpha(0f)
                    .translationY(100f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            visibility = View.GONE
                        }
                    })
                    .withLayer()
            }
        }
    }

    // 검색화면 설정
    fun activationSearch() {
        with(binding.inputSearchLayout) {
            visibility = View.VISIBLE
            imm.showSoftInput(this, 0)
        }
    }

    fun disabledSearch() {
        binding.inputSearchLayout.visibility = View.GONE
        gViewModel.searchValue.value = ""
    }
}