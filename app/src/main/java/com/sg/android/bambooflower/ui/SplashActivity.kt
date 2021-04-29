package com.sg.android.bambooflower.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.viewmodel.loginFragment.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val mViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 로그인 되어있는지 확인
        if (mViewModel.isLogin()) {
            login()
        } else {
            goMainActivity(makeIntent())
        }
    }

    override fun finish() {
        super.finish()

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun login() {
        mViewModel.getUserData()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)

                val intent = makeIntent(user != null)
                goMainActivity(intent)
            }
    }

    private fun makeIntent(isExistUser: Boolean = false) =
        Intent(this, MainActivity::class.java).apply {
            putExtra(Contents.EXTRA_IS_LOGIN, isExistUser)
        }

    // 메인 액티비티로 넘어감
    private fun goMainActivity(intent: Intent) {
        startActivity(intent)
        finish()
    }
}