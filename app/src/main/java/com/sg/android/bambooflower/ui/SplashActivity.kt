package com.sg.android.bambooflower.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.dialog.NotificationDialog
import com.sg.android.bambooflower.viewmodel.signUpFrag.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint

// TODO:
//  . 업데이트 확인 구현 (나중에)
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val mViewModel by viewModels<SignUpViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObserver()

        // 로그인 되어있는지 확인
        if (mViewModel.isLogin()) {
            // 서버 점검 확인 및 유저 데이터 존재여부 확인
            mViewModel.checkServerAndUserData()
        } else {
            goMainActivity(makeIntent())
        }
    }

    override fun finish() {
        super.finish()

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    // 옵저버 설정
    private fun setObserver() {
        // 서버 알림
        mViewModel.notification.observe(this) { notification ->
            if (notification != null) {
                NotificationDialog(notification)
                    .show(supportFragmentManager, "")
            }
        }
        // 유저 존재여부
        mViewModel.isExistUser.observe(this) { isExist ->
            if (isExist != null) {
                val intent = makeIntent(isExist)
                goMainActivity(intent)
            }
        }
        // 오류 여부
        mViewModel.isError.observe(this) { isError ->
            if (isError) {
                Toast.makeText(this, ErrorMessage.CONNECT_ERROR, Toast.LENGTH_SHORT)
                    .show()
                mViewModel.isError.value = false
            }
        }
    }

    // 인텐트 생성
    private fun makeIntent(isExistUser: Boolean = false) =
        Intent(this, MainActivity::class.java).apply {
            if (!isExistUser) {
                signOut() // 유저가 존재하지 않을 때 로그아웃
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