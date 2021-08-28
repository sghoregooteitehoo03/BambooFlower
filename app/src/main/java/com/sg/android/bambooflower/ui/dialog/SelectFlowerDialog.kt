package com.sg.android.bambooflower.ui.dialog

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
import com.sg.android.bambooflower.adapter.SelectFlowerAdapter
import com.sg.android.bambooflower.data.Flower
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.databinding.DialogSelectFlowerBinding
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.selectFlowerDialog.SelectFlowerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectFlowerDialog : BottomSheetDialogFragment(), View.OnClickListener, SelectFlowerAdapter.FlowerItemListener {
    private val mViewModel by viewModels<SelectFlowerViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    private lateinit var flowerAdapter: SelectFlowerAdapter
    private lateinit var user: User
    private lateinit var flower: Flower

    private var previousPos = -1 // 꽃을 선택한 이전 위치

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = DialogSelectFlowerBinding.inflate(inflater)
        user = gViewModel.user.value!!
        flower = gViewModel.flower.value!!
        flowerAdapter = SelectFlowerAdapter().apply {
            setOnItemListener(this@SelectFlowerDialog)
        }

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.clickListener = this@SelectFlowerDialog
            this.flowerList.adapter = flowerAdapter

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)

        setObserver() // 옵저버 설정
    }

    // 버튼 액션
    override fun onClick(v: View) {
        when (v.id) {
            R.id.close_btn -> { // 닫기 버튼
                findNavController().navigateUp()
            }
            R.id.select_btn -> { // 선택 버튼
                val flowerName = flowerAdapter.getData(
                    mViewModel.selectPos.value!!
                ).name
                mViewModel.selectFlower(user, flowerName)
            }
        }
    }

    // 꽃 선택
    override fun onItemClickListener(pos: Int) {
        if (!mViewModel.selectLoading.value!!) {
            // 선택버튼을 누르지 않았을 때만 동작
            mViewModel.selectPos.value = pos
        }
    }

    private fun setObserver() {
        // 선택창 로딩
        mViewModel.selectLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                dialog?.setCancelable(false)
            } else {
                dialog?.setCancelable(true)
            }
        }
        // 리스트 로딩여부
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                // 유저가 가지고 있는 꽃을 가져옴
                mViewModel.getHavenFlowerData(user.uid)
            }
        }
        // 유저 꽃 리스트
        mViewModel.flowerList.observe(viewLifecycleOwner) { list ->
            if (list != null) { // 리스트를 가져왔을 때
                flowerAdapter.syncData(list)
            }
        }
        // 오류 여부
        mViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                Toast.makeText(requireContext(), ErrorMessage.CONNECT_ERROR, Toast.LENGTH_SHORT)
                    .show()
                mViewModel.isError.value = false
            }
        }
        // 꽃을 클릭한 위치
        mViewModel.selectPos.observe(viewLifecycleOwner) { pos ->
            if (pos != -1) {
                flowerAdapter.getData(pos).isSelected = !flowerAdapter.getData(pos).isSelected

                if (previousPos != -1) {
                    // 이전에 눌렀던것을 해지함
                    flowerAdapter.getData(pos).isSelected = false
                    flowerAdapter.notifyItemChanged(previousPos)
                }

                flowerAdapter.notifyItemChanged(pos)
                previousPos = if (previousPos == pos) { // 같은 리스트를 눌렀을 때
                    mViewModel.selectPos.value = -1 // 선택한 위치 초기화
                    -1 // 이전 값 초기화
                } else { // 다른 리스트를 눌렀을 때
                    pos
                }
            }
        }
        // 선택한 꽃
        mViewModel.selectedFlower.observe(viewLifecycleOwner) { flowerData ->
            if (flowerData != null) {
                gViewModel.user.value = user
                gViewModel.flower.value = flowerData

                findNavController().navigateUp()
            }
        }
    }
}