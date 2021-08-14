package com.sg.android.bambooflower.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.viewmodel.signUpFrag.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint

// TODO:
//  . 서버 점검 확인 구현하기 (나중에)
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val mViewModel by viewModels<SignUpViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 로그인 되어있는지 확인
        if (mViewModel.isLogin()) {
            checkUserData()
        } else {
            goMainActivity(makeIntent())
        }
    }

    override fun finish() {
        super.finish()

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    // 유저 데이터가 존재하는지 확인
    private fun checkUserData() {
        mViewModel.checkUserData().addOnSuccessListener { result ->
            val resultMap = result.data as MutableMap<*, *>
            Log.i("Check", "result: ${resultMap}")

            if ((resultMap["isExist"] as Int) != -1) { // 오류가 아닐 때
                val isExist = (resultMap["isExist"] as Int) == 1 // 유저 존재 여부 확인

                val intent = makeIntent(isExist)
                goMainActivity(intent)
            } else { // 오류 발생
                Toast.makeText(this, ErrorMessage.CONNECT_ERROR, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    // 인텐트 생성
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