package com.sg.android.bambooflower.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
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

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val gViewModel by viewModels<GlobalViewModel>()

    @Inject
    @Named(Contents.PREF_CHECK_FIRST)
    lateinit var checkPref: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private var backAvailable = true

    private val FINISH_INTERVAL_TIME = 2000L
    private var backPressedTime = 0L
    private var isExit = false
    private lateinit var backToast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 인스턴스 설정
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.mainToolbar) // 툴바 설정
        setObserver() // 옵저버 설정

        val navFrag = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navFrag.navController

        binding.bottomNavView.setupWithNavController(navController)
        binding.profileImage.setOnClickListener { // 프로필 클릭
            if (gViewModel.userImage.value != null) {
                navController.navigate(R.id.action_global_profileFragment)
            }
        }

        // Ad Init
        MobileAds.initialize(this)

        if (intent.getBooleanExtra(Contents.EXTRA_IS_LOGIN, false)) {
            // 로그인 되어있으면 홈 화면으로 넘어감
            navController.navigate(R.id.action_signUpFragment_to_missionListFragment)
            intent.putExtra(Contents.EXTRA_IS_LOGIN, false)
        } else if (checkPref.getBoolean(Contents.PREF_KEY_IS_FIRST, true)) {
            // 처음 앱을 킨 유저일 시 온보딩 화면으로 이동
            navController.navigate(R.id.action_global_onboardFragment)
        }
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.missionListFragment, R.id.postListFragment, R.id.diaryListFragment, R.id.rankingFragment -> {
                    isExit = true

                    showBottomView()
                    showProfile()
                }
                R.id.acceptListDialog, R.id.levelUpDialog, R.id.missionDialog, R.id.reportDialog -> {
                    isExit = false
                }
                else -> {
                    isExit = false

                    hideBottomView()
                    hideProfile()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios)
    }

    override fun onBackPressed() {
        if (backAvailable) {
            if (isExit) {
                val tempTime = System.currentTimeMillis()
                val intervalTime = tempTime - backPressedTime

                if (intervalTime in 0..FINISH_INTERVAL_TIME) {
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

    private fun setObserver() {
        gViewModel.userImage.observe(this) { image ->
            if (image != null) {
                binding.profileImage.visibility = View.VISIBLE

                if (image.isEmpty()) {
                    Glide.with(applicationContext)
                        .load(R.drawable.ic_person)
                        .into(binding.profileImage)
                } else {
                    Glide.with(applicationContext)
                        .load(image)
                        .into(binding.profileImage)
                }
            } else {
                binding.profileImage.visibility = View.GONE
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

    fun showToolbar(isDivide: Boolean = true) {
        supportActionBar?.show()
        binding.divideView.visibility = if (isDivide) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun showProfile() {
        if (binding.profileImage.visibility == View.GONE &&
            gViewModel.userImage.value != null
        ) {
            binding.profileImage.visibility = View.VISIBLE
        }
    }

    private fun hideProfile() {
        if (binding.profileImage.visibility == View.VISIBLE) {
            binding.profileImage.visibility = View.GONE
        }
    }

    private fun showBottomView() {
        with(binding.bottomNavView) {
            if (visibility == View.GONE) {
                visibility = View.VISIBLE
            }
        }
    }

    private fun hideBottomView() {
        with(binding.bottomNavView) {
            if (visibility == View.VISIBLE) {
                visibility = View.GONE
            }
        }
    }

    fun enableBottomView() {
        binding.bottomNavView
            .menu
            .forEach {
                it.isEnabled = true
            }
    }

    fun unEnableBottomView() {
        binding.bottomNavView
            .menu
            .forEach {
                it.isEnabled = false
            }
    }
}