package com.sg.android.bambooflower.ui.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.QuestImageAdapter
import com.sg.android.bambooflower.databinding.DialogMyQuestBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.SecondActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.myQuestDialog.MyQuestViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyQuestDialog : BottomSheetDialogFragment(),
    QuestImageAdapter.ImageItemListener,
    View.OnClickListener {

    private val mViewModel by viewModels<MyQuestViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    private lateinit var missionImageAdapter: QuestImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = DialogMyQuestBinding.inflate(inflater)
        val usersQuest = gViewModel.usersQuest.value!!
        missionImageAdapter = QuestImageAdapter(usersQuest.quest.images).apply {
            setOnImageItemListener(this@MyQuestDialog)
        }

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.gviewmodel = gViewModel
            this.clickListener = this@MyQuestDialog

            this.missionWayImageList.adapter = missionImageAdapter

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)

        setObserver()
    }

    // 사진 리스트 아이템 클릭
    override fun onItemClickListener(pos: Int) {
        val images = missionImageAdapter.getList()
        showImages(images, pos)
    }

    // 버튼 액션
    override fun onClick(v: View) {
        when (v.id) {
            R.id.close_btn -> {
                findNavController().navigateUp()
            }
            R.id.action_btn -> { // 보상받기
                getReward()
            }
            R.id.give_up_btn -> { // 포기하기
                val usersQuestId = gViewModel.usersQuest.value!!.id
                mViewModel.giveUpQuest(usersQuestId)
            }
            R.id.certify_btn -> { // 인증하기
                findNavController().navigate(R.id.action_myQuestDialog_to_addPostFragment)
            }
        }
    }

    private fun setObserver() {
        // 오류 여부
        mViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                Toast.makeText(requireContext(), ErrorMessage.CONNECT_ERROR, Toast.LENGTH_SHORT)
                    .show()
                mViewModel.isError.value = false
            }
        }
        // 버튼 로딩
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                dialog?.setCancelable(false)
            } else {
                dialog?.setCancelable(true)
            }
        }
        // 퀘스트 포기 완료
        mViewModel.isGiveUpComplete.observe(viewLifecycleOwner) { complete ->
            if (complete) {
                val usersQuestList = gViewModel.usersQuestList.value!!
                usersQuestList.remove(gViewModel.usersQuest.value!!) // 유저 퀘스트목록에서 퀘스트 지움

                gViewModel.usersQuestList.value = usersQuestList
                gViewModel.isDeleteQuest.value = true
                findNavController().navigateUp()
            }
        }
    }

    // 이미지 액티비티로 이동
    private fun showImages(images: List<String>, pos: Int) {
        SecondActivity.images = images

        val intent = Intent(requireContext(), SecondActivity::class.java).apply {
            putExtra(Contents.EXTRA_SET_POS, pos)
            action = Contents.SHOW_IMAGE_FRAG
        }
        startActivity(intent)
    }

    // 보상 받기
    private fun getReward() {
        val flowerState = gViewModel.flower.value!!.state

        if (flowerState != 0) { // 유저가 성장시키고 있는 꽃이 존재할 때
            findNavController().navigateUp()
            findNavController().navigate(R.id.flowerStateDialog)
        } else {
            gViewModel.moveLayout.value = R.id.flowerStateDialog // 선택 후 보상화면으로 이동되게 설정
            Toast.makeText(requireContext(), "꽃을 먼저 선택해야 보상을 얻으실 수 있습니다.", Toast.LENGTH_SHORT)
                .show()

            findNavController().navigateUp()
            findNavController().navigate(R.id.selectFlowerDialog)
        }
    }
}