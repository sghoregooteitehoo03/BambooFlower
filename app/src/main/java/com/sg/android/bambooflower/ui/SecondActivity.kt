package com.sg.android.bambooflower.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.databinding.ActivitySecondBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.ui.fragment.EmptyFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding
    private lateinit var navController: NavController

    companion object {
        var images: List<String>? = null
    }

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
            Contents.SHOW_IMAGE_FRAG -> {
                navController.navigate(R.id.action_emptyFragment_to_imageDetailFragment)
            }
            Contents.SHOW_WEB_VIEWER -> {
                val title = intent.getStringExtra(Contents.EXTRA_TITLE)

                if (title != null) {
                    val directions = EmptyFragmentDirections
                        .actionEmptyFragmentToHtmlViewerFragment(title)
                    navController.navigate(directions)
                } else {
                    finish()
                }
            }
            else -> {
            }
        }
    }
}