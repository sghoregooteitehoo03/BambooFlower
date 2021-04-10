package com.sg.android.bambooflower.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.databinding.ActivityMainBinding
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import dagger.hilt.android.AndroidEntryPoint

//  버그
//  1.bottom nav을 이용한 화면 전환시 상태 저장기능 구현
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val gViewModel by viewModels<GlobalViewModel>()

    private lateinit var binding: ActivityMainBinding
    private lateinit var imm: InputMethodManager
    private var backAvailable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 인스턴스 설정
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        imm =
            applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // 툴바 설정
        setSupportActionBar(binding.mainToolbar)

        val navController = findNavController(R.id.nav_host_fragment)

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

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.homeFragment, R.id.rankingFragment, R.id.profileFragment -> {
                    binding.bottomNavView.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomNavView.visibility = View.GONE
                }
            }
        }
    }

    override fun onBackPressed() {
        if (backAvailable) {
            super.onBackPressed()
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