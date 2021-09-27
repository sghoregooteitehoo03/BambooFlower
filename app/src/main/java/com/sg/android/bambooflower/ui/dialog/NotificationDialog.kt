package com.sg.android.bambooflower.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.data.NotificationData
import com.sg.android.bambooflower.databinding.DialogNotificationBinding

class NotificationDialog(
    private val notification: NotificationData
) : BottomSheetDialogFragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = DialogNotificationBinding.inflate(inflater)

        // 바인딩 설정
        with(binding) {
            message = notification.message
            btnText = if (notification.action == "QUIT") {
                "확인"
            } else {
                "업데이트"
            }
            clickListener = this@NotificationDialog
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.check_btn -> {
                if (notification.action == "QUIT") {
                    requireActivity().finish()
                } else {
                    // TODO: 플레이스토어로 이동
                }
            }
        }
    }
}