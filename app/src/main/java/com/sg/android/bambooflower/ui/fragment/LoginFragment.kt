package com.sg.android.bambooflower.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.databinding.FragmentLoginBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.loginFragment.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val mViewModel by viewModels<LoginViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    private lateinit var callbackManager: CallbackManager
    private lateinit var loginWay: String
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentLoginBinding.inflate(inflater)
        callbackManager = CallbackManager.Factory.create()
        gViewModel.user.value = null

        // 바인딩 설정
        with(binding) {
            viewmodel = mViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    override fun onStart() {
        super.onStart()

        // 툴바 설정
        with((activity as MainActivity).supportActionBar) {
            this?.title = ""
            this?.setDisplayHomeAsUpEnabled(false)
        }
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
                    loading()

                    token = result.result?.idToken!!
                    loginWay = "Google"

                    val credential =
                        GoogleAuthProvider.getCredential(token, null)
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
        // 오류 발생 시
        mViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                Toast.makeText(requireContext(), "서버와 연결 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT)
                    .show()
                mViewModel.isError.value = false
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
                loading()

                token = result?.accessToken!!.token
                loginWay = "Facebook"

                val credential = FacebookAuthProvider.getCredential(token)
                mViewModel.login(credential)
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {
                error?.printStackTrace()
            }
        })
    }

    // 로그인 성공
    private fun successLogin() {
        mViewModel.getUserData()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                ready()

                if (user != null) { // 기존 유저인지 확인
                    findNavController().navigate(R.id.action_loginFragment_to_missionFragment)
                } else {
                    val directions = LoginFragmentDirections
                        .actionLoginFragmentToCreateUserFragment(token, loginWay)
                    findNavController().navigate(directions)
                }
            }
            .addOnFailureListener {
                mViewModel.isError.value = true
            }
    }

    private fun loading() {
        (requireActivity() as MainActivity).loading()
    }

    private fun ready() {
        (requireActivity() as MainActivity).ready()
    }
}