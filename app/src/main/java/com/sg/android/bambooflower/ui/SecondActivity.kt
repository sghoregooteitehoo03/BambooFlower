package com.sg.android.bambooflower.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 인스턴스 설정
        binding = DataBindingUtil.setContentView(this, R.layout.activity_second)

        // 툴바 설정
        setSupportActionBar(binding.mainToolbar)

        navController = findNavController(R.id.nav_host_fragment)

        checkAction()
    }

    private fun checkAction() {
        when (intent.action) {
            else -> {
            }
        }
    }
}