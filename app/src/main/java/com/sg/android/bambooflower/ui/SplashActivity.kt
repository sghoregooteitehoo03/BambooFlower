package com.sg.android.bambooflower.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.viewmodel.signUpFrag.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val mViewModel by viewModels<SignUpViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 서버 점검 확인
        mViewModel.checkServer().addOnSuccessListener {
            if (!(it["serverCheck"] as Boolean)) {
                // 로그인 되어있는지 확인
                if (mViewModel.isLogin()) {
                    login()
                } else {
                    goMainActivity(makeIntent())
                }
            } else {
                // 점검 중일 때
                with(MaterialAlertDialogBuilder(this)) {
                    setMessage(it["serverCheckMsg"].toString())
                    setPositiveButton("확인") { dialog, which ->
                        finish()
                    }
                    setCancelable(false)

                    show()
                }
            }
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
            if (!isExistUser) {
                signOut()
            }
            putExtra(Contents.EXTRA_IS_LOGIN, isExistUser)
        }

    // 메인 액티비티로 넘어감
    private fun goMainActivity(intent: Intent) {
        startActivity(intent)
        finish()
    }

    // 계정 로그아웃
    private fun signOut() {
        mViewModel.signOut(this)
    }
}