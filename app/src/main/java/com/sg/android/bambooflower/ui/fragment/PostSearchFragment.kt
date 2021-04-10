package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.sg.android.bambooflower.databinding.FragmentPostSearchBinding
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.postSearchFragment.PostSearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostSearchFragment : Fragment() {
    private val mViewModel by viewModels<PostSearchViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostSearchBinding.inflate(inflater)

        with(binding) {
           viewmodel = mViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    private fun setObserver() {
        gViewModel.searchValue.observe(viewLifecycleOwner) {
            Log.i("Check", "value: $it")
        }
    }
}