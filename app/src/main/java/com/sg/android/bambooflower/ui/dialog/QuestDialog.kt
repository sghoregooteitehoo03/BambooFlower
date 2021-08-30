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
        val isNull = gViewModel.usersQuestList.value == null // 게시글에서 넘어왔는지 확인
        missionImageAdapter = QuestImageAdapter(listOf()).apply {
            setOnImageItemListener(this@QuestDialog)
        }

        if (isNull) {
            mViewModel.isLoading.value = true
        } else {
            val usersQuest = gViewModel.usersQuest.value!!

            mViewModel.questData.value = usersQuest.quest
            mViewModel.userQuestSize.value = gViewModel.usersQuestList.value!!.size
            mViewModel.userQuestExists.value = gViewModel.usersQuestList.value!!
                .contains(usersQuest)
        }


        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.clickListener = this@QuestDialog

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
            R.id.action_btn -> {
                val userId = gViewModel.user.value!!.uid
                val questId = mViewModel.questData.value!!.id

                mViewModel.acceptQuest(userId, questId)
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
        // 메인 로딩
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) { // 게시글에서 넘어온 경우 퀘스트 데이터를 읽어옴
                val questId = gViewModel.post.value!!.questId
                val uid = gViewModel.user.value!!.uid

                mViewModel.getQuest(uid, questId)
            }
        }
        // 버튼 로딩
        mViewModel.buttonLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                dialog?.setCancelable(false)
            } else {
                dialog?.setCancelable(true)
            }
        }
        // 퀘스트 데이터
        mViewModel.questData.observe(viewLifecycleOwner) { quest ->
            if (quest != null) {
                missionImageAdapter.syncData(quest.images)
            }
        }
        // 퀘스트 수락 완료
        mViewModel.isAcceptComplete.observe(viewLifecycleOwner) { result ->
            if (result != -1) {
                val usersQuestList = gViewModel.usersQuestList.value
                if (usersQuestList != null) {
                    val usersQuest = gViewModel.usersQuest.value!!.apply {
                        id = result
                        state = 1
                        timestamp = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
                            .format(System.currentTimeMillis())
                            .toInt()
                    }
                    usersQuestList.add(usersQuest) // 유저 퀘스트목록에 퀘스트 추가

                    gViewModel.usersQuestList.value = usersQuestList
                }

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