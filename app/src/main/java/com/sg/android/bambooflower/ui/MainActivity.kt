package com.sg.android.bambooflower.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.MobileAds
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.databinding.ActivityMainBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

// TODO:
//  . bottom navigation 애니메이션 없애기 (여유있을 때 찾아보고 적용하기)
//  . 유저한테 경고하는 화면 구현 (다음 업데이트 때)
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val gViewModel by viewModels<GlobalViewModel>()

    @Inject
    @Named(Contents.PREF_CHECK_FIRST)
    lateinit var checkPref: SharedPreferences

    @Inject
    @Named(Contents.PREF_SETTING)
    lateinit var settingPref: SharedPreferences

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
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
        setActive(true) // 액티비티 활성화 여부

        val navFrag = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navFrag.navController

        binding.bottomNavView.setupWithNavController(navController)
        binding.pointLayout.setOnClickListener { // 포인트 클릭
            if (gViewModel.user.value != null) {
                navController.navigate(R.id.shopFragment)
            }
        }

        // Ad Init
        MobileAds.initialize(this)

        // 프래그먼트 이동
        moveFragment(intent)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.homeFragment, R.id.postListFragment, R.id.profileFragment -> {
                    isExit = true

                    showBottomView()
                    showPoint()
                }
                R.id.selectFlowerDialog, R.id.cheerListDialog, R.id.questDialog, R.id.reportDialog -> {
                    isExit = false
                }
                else -> {
                    isExit = false

                    hideBottomView()
                    hidePoint()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios)
    }

    override fun onStop() {
        super.onStop()
        setActive(false)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        moveFragment(intent)
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

    private fun moveFragment(newIntent: Intent?) {
        if (newIntent?.getBooleanExtra(Contents.EXTRA_IS_LOGIN, false) == true) {
            // 로그인 되어있으면 홈 화면으로 넘어감
            navController.navigate(R.id.action_signUpFragment_to_homeFragment)
            newIntent.putExtra(Contents.EXTRA_IS_LOGIN, false)
        } else if (newIntent?.action == Contents.SHOW_HOME_FRAG) {
            navController.navigate(R.id.action_global_homeFragment)
        } else if (checkPref.getBoolean(Contents.PREF_KEY_IS_FIRST, true)) {
            // 처음 앱을 킨 유저일 시 온보딩 화면으로 이동
            navController.navigate(R.id.action_global_onboardFragment)
        }
    }

    private fun setObserver() {
        gViewModel.user.observe(this) { user ->
            binding.pointText.text = user?.money?.toString() ?: ". . ."
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

    fun hideToolbar() {
        supportActionBar?.hide()
        binding.divideView.visibility = View.GONE
    }

    private fun showPoint() {
        if (binding.pointLayout.visibility == View.GONE) {
            binding.pointLayout.visibility = View.VISIBLE
        }
    }

    private fun hidePoint() {
        if (binding.pointLayout.visibility == View.VISIBLE) {
            binding.pointLayout.visibility = View.GONE
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

    private fun setActive(isActive: Boolean) {
        with(settingPref.edit()) {
            putBoolean(Contents.PREF_KEY_IS_ACTIVE, isActive)
            commit()
        }
    }
}