package com.sg.android.bambooflower.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.databinding.FragmentLoginBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.viewmodel.loginFragment.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

// TODO: 디자인 및 자잘한 기능 추가
@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val mViewModel by viewModels<LoginViewModel>()

    private lateinit var callbackManager: CallbackManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLoginBinding.inflate(inflater)
        with(binding) {
            viewmodel = mViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 인스턴스 설정
        callbackManager = CallbackManager.Factory.create()

        setObserver()
    }

    override fun onDestroyView() {
        mViewModel.clear()
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Contents.LOGIN_WITH_GOOGLE -> {
                val result = GoogleSignIn.getSignedInAccountFromIntent(data)
                if (result.isSuccessful) {

                    val credential =
                        GoogleAuthProvider.getCredential(result.result?.idToken!!, null)
                    mViewModel.login(credential)
                }
            }
        }
    }

    private fun setObserver() { // 옵저버 설정
        // 로그인 방법
        mViewModel.loginWay.observe(viewLifecycleOwner) { it ->
            when (it) {
                Contents.LOGIN_WITH_EMAIL -> {
                    loginEmail()
                }
                Contents.LOGIN_WITH_GOOGLE -> {
                    loginGoogle()
                }
                Contents.LOGIN_WITH_FACEBOOK -> {
                    loginFacebook()
                }
                else -> {
                }
            }
        }
        // 로그인 성공 시
        mViewModel.isSuccessLogin.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                successLogin()
            }
        }
    }

    // 이메일로 로그인
    private fun loginEmail() {
        findNavController().navigate(R.id.action_loginFragment_to_emailLoginFragment)
    }

    private fun loginGoogle() { // 구글 로그인
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignClient = GoogleSignIn.getClient(requireContext(), gso)

        startActivityForResult(googleSignClient.signInIntent, Contents.LOGIN_WITH_GOOGLE)
    }

    // 페이스북 로그인
    private fun loginFacebook() {
        val loginManager = LoginManager.getInstance().apply {
            logInWithReadPermissions(this@LoginFragment, listOf("email", "public_profile"))
        }

        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                val credential = FacebookAuthProvider.getCredential(result?.accessToken!!.token)
                mViewModel.login(credential)
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {
                error?.printStackTrace()
            }
        })
    }

    private fun successLogin() {
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }
}