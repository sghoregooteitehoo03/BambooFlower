package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.sg.android.bambooflower.adapter.GuideAdapter
import com.sg.android.bambooflower.databinding.FragmentGuideBinding
import com.sg.android.bambooflower.ui.MainActivity

// TODO: 디자인 및 가이드 수정하기(나중에)
class GuideFragment : Fragment(), GuideAdapter.GuideItemListener {
    private lateinit var guideAdapter: GuideAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentGuideBinding.inflate(inflater)
        guideAdapter = GuideAdapter().apply {
            setOnItemListener(this@GuideFragment)
        }

        // 바인딩 설정
        with(binding) {
            with(guideList) {
                adapter = guideAdapter
                itemAnimator = null
                addItemDecoration(
                    DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                )
            }

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onStart() {
        super.onStart()
        // 툴바 설정
        with((activity as MainActivity)) {
            supportActionBar?.title = "도움말"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            showToolbar()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> false
        }
    }

    // 가이드 열기
    override fun onItemClickListener(pos: Int) {
        guideAdapter.expandItem(pos)
    }
}