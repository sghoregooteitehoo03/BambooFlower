package com.sg.android.bambooflower.ui.dialog

import android.content.Intent
import android.icu.text.SimpleDateFormat
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
import com.sg.android.bambooflower.databinding.DialogQuestBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.SecondActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.questDialog.QuestViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

// TODO:
//  . 레벨업 구현 (나중에)
@AndroidEntryPoint
class QuestDialog : BottomSheetDialogFragment(),
    QuestImageAdapter.ImageItemListener,
    View.OnClickListener {

    private val mViewModel by viewModels<QuestViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    private lateinit var missionImageAdapter: QuestImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = DialogQuestBinding.inflate(inflater)
        val usersQuest = gViewModel.usersQuest.value!!
        missionImageAdapter = QuestImageAdapter(usersQuest.quest.images).apply {
            setOnImageItemListener(this@QuestDialog)
        }

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.gviewmodel = gViewModel
            this.clickListener = this@QuestDialog

            this.missionWayImageList.adapter = missionImageAdapter

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            R.id.action_btn -> {
                val state = gViewModel.usersQuest.value!!.state
                if (state != 0) { // 보상받기
                    // TODO: 나중에
                } else { // 수락하기
                    val userId = gViewModel.user.value!!.uid
                    val questId = gViewModel.usersQuest.value!!.quest.id

                    mViewModel.acceptQuest(userId, questId)
                }
            }
            R.id.give_up_btn -> { // 포기하기
                val usersQuestId = gViewModel.usersQuest.value!!.id
                mViewModel.giveUpQuest(usersQuestId)
            }
            R.id.certify_btn -> { // 인증하기
                findNavController().navigate(R.id.action_questDialog_to_addPostFragment)
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
        mViewModel.isActionLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                dialog?.setCanceledOnTouchOutside(false)
                dialog?.setCancelable(false)
            } else {
                dialog?.setCanceledOnTouchOutside(true)
                dialog?.setCancelable(true)
            }
        }
        // 포기버튼 로딩
        mViewModel.isGiveUpLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                dialog?.setCanceledOnTouchOutside(false)
                dialog?.setCancelable(false)
            } else {
                dialog?.setCanceledOnTouchOutside(true)
                dialog?.setCancelable(true)
            }
        }
        // 퀘스트 수락 완료
        mViewModel.isAcceptComplete.observe(viewLifecycleOwner) { result ->
            if (result != -1) {
                val usersQuestList = gViewModel.usersQuestList.value!!
                val usersQuest = gViewModel.usersQuest.value!!.apply {
                    id = result
                    state = 1
                    timestamp = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
                        .format(System.currentTimeMillis())
                        .toInt()
                }
                usersQuestList.add(usersQuest) // 유저 퀘스트목록에 퀘스트 추가

                gViewModel.usersQuestList.value = usersQuestList
                findNavController().navigateUp()
            }
        }
        // 퀘스트 포기 완료
        mViewModel.isGiveUpComplete.observe(viewLifecycleOwner) { complete ->
            if (complete) {
                val usersQuestList = gViewModel.usersQuestList.value!!
                usersQuestList.remove(gViewModel.usersQuest.value!!) // 유저 퀘스트목록에서 퀘스트 지움

                gViewModel.usersQuestList.value = usersQuestList
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
}