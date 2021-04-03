package com.sg.android.bambooflower.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

//  버그
//  1.bottom nav을 이용한 화면 전환시 상태 저장기능 구현
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var backAvailable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // 툴바 설정
        setSupportActionBar(binding.mainToolbar)

        val navController = findNavController(R.id.nav_host_fragment)

        binding.bottomNavView.setupWithNavController(navController)
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

    fun loading() {
        backAvailable = false
        binding.loadingView.setVisible(true, window)
    }

    fun ready() {
        backAvailable = true
        binding.loadingView.setVisible(false, window)
    }
}