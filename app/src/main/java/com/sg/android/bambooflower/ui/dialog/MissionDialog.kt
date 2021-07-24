package com.sg.android.bambooflower.ui.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.MissionImageAdapter
import com.sg.android.bambooflower.databinding.DialogMissionBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.ui.SecondActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.missionDialog.MissionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MissionDialog : BottomSheetDialogFragment(),
    MissionImageAdapter.ImageItemListener,
    View.OnClickListener {

    private val mViewModel by viewModels<MissionViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    private lateinit var missionImageAdapter: MissionImageAdapter
    private lateinit var imageOtherAdapter: MissionImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = DialogMissionBinding.inflate(inflater)
        missionImageAdapter = MissionImageAdapter().apply {
            setOnImageItemListener(this@MissionDialog)
        }
        imageOtherAdapter = MissionImageAdapter(true).apply {
            setOnImageItemListener(this@MissionDialog)
        }

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.mission = gViewModel.mission.value!!
            this.user = gViewModel.user.value!!
            this.clickListener = this@MissionDialog

            this.missionWayImageList.adapter = missionImageAdapter
            this.otherUserImageList.adapter = imageOtherAdapter

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet =
            requireDialog().findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED

        setObserver()
    }

    // 사진 리스트 아이템 클릭
    override fun onItemClickListener(pos: Int, isOther: Boolean) {
        val images = if (isOther) {
            imageOtherAdapter.getList()
                .map { it.toString() }
        } else {
            missionImageAdapter.getList()
                .map { it.toString() }
        }

        showImages(images, pos)
    }

    // 버튼 액션
    override fun onClick(v: View) {
        when (v.id) {
            R.id.close_btn -> {
                findNavController().navigateUp()
            }
            R.id.action_btn -> {
                findNavController().navigate(R.id.action_missionDialog_to_addPostFragment)
            }
        }
    }

    private fun setObserver() {
        mViewModel.otherUserImageList.observe(viewLifecycleOwner) { list ->
            if (list != null) {
                val mission = gViewModel.mission.value!!

                missionImageAdapter.syncData(mission.missionImage!!.map { it.toUri() })
                imageOtherAdapter.syncData(list)
            } else {
                val mission = gViewModel.mission.value!!
                mViewModel.getOtherUserImages(mission.document!!)
            }
        }
    }

    // 이미지 액티비티로 이동
    private fun showImages(images: List<String>, pos: Int) {
        val intent = Intent(requireContext(), SecondActivity::class.java).apply {
            putExtra(Contents.EXTRA_SET_IMAGE, images.toTypedArray())
            putExtra(Contents.EXTRA_SET_POS, pos)
            action = Contents.SHOW_IMAGE_FRAG
        }
        startActivity(intent)
    }
}