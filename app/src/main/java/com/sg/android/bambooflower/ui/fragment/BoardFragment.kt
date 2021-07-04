package com.sg.android.bambooflower.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sg.android.bambooflower.databinding.FragmentBoardBinding

class BoardFragment(
    private val imageRes: Int,
    private val title: String,
    private val content: String
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBoardBinding.inflate(inflater)

        with(binding) {
            boardImage.setImageResource(imageRes)
            boardTitle.text = title
            boardContent.text = content
        }

        return binding.root
    }
}