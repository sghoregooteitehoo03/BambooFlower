package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sg.android.bambooflower.databinding.FragmentHtmlViewerBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.ui.SecondActivity
import com.sg.android.bambooflower.viewmodel.htmlViewerFragment.HtmlViewerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HtmlViewerFragment : Fragment() {
    private val mViewModel by viewModels<HtmlViewerViewModel>()
    private val args by navArgs<HtmlViewerFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // 인스턴스 설정
        val binding = FragmentHtmlViewerBinding.inflate(inflater)

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel

            webview.webViewClient = webCallback
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        mViewModel.readHtml(args.title)
    }

    override fun onStart() {
        super.onStart()
        // 툴바 설정
        with((activity as SecondActivity).supportActionBar) {
            this?.show()
            this?.setDisplayHomeAsUpEnabled(true)
            this?.title = when(args.title) {
                Contents.CHILD_TERMS_OF_SERVICE -> "이용약관"
                Contents.CHILD_PERSONAL_INFORMATION -> "개인정보 수집 및 이용"
                Contents.CHILD_PRIVACY_POLICY -> "개인정보처리방침"
                else -> ""
            }
        }
    }

    // 메뉴 설정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().finish()
                true
            }
            else -> false
        }
    }

    private val webCallback = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            mViewModel.isLoaded.value = false
        }
    }
}