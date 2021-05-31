package com.sg.android.bambooflower.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.sg.android.bambooflower.databinding.DialogLevelUpBinding

class LevelUpDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // 인스턴스 설정
        val binding = DialogLevelUpBinding.inflate(inflater)

        // 바인딩 설정
        with(binding) {
            this.navController = findNavController()

            lifecycleOwner = viewLifecycleOwner
        }

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }
}