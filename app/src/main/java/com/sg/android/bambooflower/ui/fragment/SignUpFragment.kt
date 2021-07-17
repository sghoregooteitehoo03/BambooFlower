package com.sg.android.bambooflower.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.databinding.FragmentSignUpBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.signUpFrag.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment(), View.OnClickListener {
    private val mViewModel by viewModels<SignUpViewModel>()

    private lateinit var callbackManager: CallbackManager
    private var loginWay = ""
    private var token = ""
    private var email = ""
    private var password = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentSignUpBinding.inflate(inflater)
        callbackManager = CallbackManager.Factory.create()

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.blank = ""
            this.clickListener = this@SignUpFragment

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
                    mViewModel.isLoading.value = true

                    token = result.result?.idToken!!
                    loginWay = "Google"

                    val credential =
                        GoogleAuthProvider.getCredential(token, null)
                    mViewModel.login(credential)
                }
            }
        }
    }

    // 버튼 액션
    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_up_btn -> { // 가입하기 버튼
                signUp()
            }
            R.id.facebook_login -> { // 페이스북 로그인
                loginFacebook()
            }
            R.id.google_login -> { // 구글 로그인
                loginGoogle()
            }
            R.id.email_login -> { // 이메일 로그인
                loginEmail()
            }
        }
    }

    private fun setObserver() { // 옵저버 설정
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
        // 로딩여부
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                (requireActivity() as MainActivity).loading()
            } else {
                (requireActivity() as MainActivity).ready()
            }
        }
    }

    // 회원가입
    private fun signUp() {
        loginWay = "Email"
        email = mViewModel.email.value!!
        password = mViewModel.password.value!!

        mViewModel.signUp()
    }

    // 이메일로 로그인
    private fun loginEmail() {
        findNavController().navigate(R.id.action_signUpFragment_to_emailLoginFragment)
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
            logInWithReadPermissions(this@SignUpFragment, listOf("email", "public_profile"))
        }

        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                mViewModel.isLoading.value = true

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
                mViewModel.isLoading.value = false

                if (user != null) { // 기존 유저인지 확인
                    findNavController().navigate(R.id.action_signUpFragment_to_missionFragment)
                } else {
                    val directions = SignUpFragmentDirections
                        .actionSignUpFragmentToCreateUserFragment(token, loginWay, password, email)
                    findNavController().navigate(directions)
                }
            }
            .addOnFailureListener {
                mViewModel.isError.value = true
            }
    }
}