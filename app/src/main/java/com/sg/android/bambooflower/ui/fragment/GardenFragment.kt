package com.sg.android.bambooflower.ui.fragment

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.CollocateAdapter
import com.sg.android.bambooflower.adapter.InventoryAdapter
import com.sg.android.bambooflower.data.Garden
import com.sg.android.bambooflower.data.Inventory
import com.sg.android.bambooflower.databinding.FragmentGardenBinding
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.gardenFrag.GardenViewModel
import dagger.hilt.android.AndroidEntryPoint

// TODO:
//  . 꽃 사용 O
//  . 이미 배치된 아이템 바꾸기 O
//  . 범위 밖으로 나갈 시 기존에 있던 자리로 O
//  . 아이템 인벤토리로 다시 가져오게 O
//  . 아이템 인벤토리로 가져올 때 보여줄 이미지 (나중에)
//  . 벽지 적용 O
//  . 벽지 사용 O
//  . 저장 기능 O
//  . 불러오기 기능 O
//  . 벽지 사용중 표시 O
//  . 디바이스마다 불러오기 기능 적용 (나중에)

@AndroidEntryPoint
class GardenFragment : Fragment(), View.OnClickListener,
    InventoryAdapter.InventoryItemListener, CollocateAdapter.CollocateItemListener {
    private val mViewModel by viewModels<GardenViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    private lateinit var collocateAdapter: CollocateAdapter
    private lateinit var inventoryAdapter: InventoryAdapter
    private lateinit var motionLayoutView: MotionLayout
    private lateinit var gardenLayoutView: FrameLayout

    private var inventoryData: Inventory? = null // 드래그 하고있는 아이템의 데이터
    private var collocatedImageView: ImageView? = null // 배치된 이미지뷰의 데이터
    private var action: String = "" // 배치 액션
    private var isFirst = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentGardenBinding.inflate(inflater)
        collocateAdapter = CollocateAdapter().apply {
            setOnItemListener(this@GardenFragment)
        }
        inventoryAdapter = InventoryAdapter().apply {
            setOnItemListener(this@GardenFragment)
        }

        activity?.onBackPressedDispatcher?.addCallback(backPressed)

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.clickListener = this@GardenFragment

            this.listLayout.setOnDragListener(inventoryDragListener)
            with(this.collocateList) {
                adapter = collocateAdapter
                itemAnimator = null
            }
            with(this.inventoryList) {
                adapter = inventoryAdapter
                itemAnimator = null
            }
            motionLayoutView = this.motionLayout
            gardenLayoutView = this.gardenLayout

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    override fun onStart() {
        super.onStart()
        // 툴바 설정
        with((activity as MainActivity)) {
            supportActionBar?.title = ""
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            hideToolbar()
        }
    }

    override fun onDestroy() {
        backPressed.isEnabled = false
        super.onDestroy()
    }

    // 버튼 액션
    override fun onClick(v: View) {
        when (v.id) {
            R.id.back_btn -> { // 뒤로가기 버튼
                val isEdited = mViewModel.isEdited.value!!
                if (isEdited) {
                    showExitDialog()
                } else {
                    findNavController().navigateUp()
                }
            }
            R.id.save_text -> { // 저장 버튼
                mViewModel.saveGardenData()
                Toast.makeText(requireContext(), "저장되었습니다.", Toast.LENGTH_SHORT)
                    .show()
            }
            R.id.filter_flower -> { // 꽃 필터
                val category = mViewModel.category.value!!
                if (category != Inventory.ITEM_CATEGORY_FLOWER) {
                    mViewModel.category.value = Inventory.ITEM_CATEGORY_FLOWER
                }
            }
            R.id.filter_wallpaper -> { // 벽지 필터
                val category = mViewModel.category.value!!
                if (category != Inventory.ITEM_CATEGORY_WALLPAPER) {
                    mViewModel.category.value = Inventory.ITEM_CATEGORY_WALLPAPER
                }
            }
            R.id.close_inventory_btn -> { // 닫기 버튼
                mViewModel.isExpand.value = !mViewModel.isExpand.value!!
            }
        }
    }

    // 정원 드래그 리스너
    override fun onItemDragListener(view: View, event: DragEvent, pos: Int) {
        when (event.action) {
            DragEvent.ACTION_DRAG_ENTERED -> { // 뷰에 드래그 아이템 진입
                collocateAdapter.setTileList(pos, true)
            }
            DragEvent.ACTION_DRAG_EXITED -> { // 뷰에 드래그 아이템 나감
                collocateAdapter.setTileList(pos, false)
            }
            DragEvent.ACTION_DROP -> { // 해당 뷰에 드래그 아이템 드롭
                // 배치 되어있지 않은곳에서만 동작
                if (!collocateAdapter.isCollocated(pos)) {
                    val gardenList = mViewModel.savedGardenList.value!!
                    if (action == "ADD") { // 새로운 아이템음 배치하고 있는 경우
                        val gardenData = Garden(
                            null,
                            inventoryData?.id!!,
                            inventoryData!!.itemIcon,
                            inventoryData!!.category,
                            pos,
                            view.x,
                            view.y
                        )
                        // 이미지 배치
                        collocateImage(gardenData)

                        // 아이템 사용
                        val useItemPos = mViewModel.useItem(inventoryData!!)

                        inventoryAdapter.notifyItemChanged(useItemPos)
                        gardenList.add(gardenData)
                    } else { // 배치된 아이템을 수정하려고 하는 경우
                        val tagSplit = getTagSplit(collocatedImageView!!.tag.toString())
                        val index = gardenList.indexOf(
                            Garden(0, tagSplit[0], "", tagSplit[1], tagSplit[2], 0f, 0f)
                        )
                        Log.i("Check", "index: $index")
                        // 기존에 있던 아이템의 위치를 수정함
                        val gardenData = gardenList[index].apply {
                            this.collocatedPos = pos
                            this.collocatedX = view.x
                            this.collocatedY = view.y
                        }

                        collocateImage(gardenData)
                    }

                    mViewModel.isEdited.value = true
                    collocateAdapter.setCollocatedList(pos, true)
                } else {
                    if (action == "EDIT") { // 이미 배치된 아이템을 배치하고 있는 경우
                        previousCollocatedImage(false) // 기존에 있던 자리로 다시 세팅
                    }
                    Toast.makeText(requireContext(), "해당 위치에는 배치할 수 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                }

                // 초기화
                inventoryData = null
                collocatedImageView = null
                collocateAdapter.setTileList(pos, false)
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                // 배치된 이미지를 수정하고 있는 상태에서 배치 범위를 벗어난곳에 드롭한 경우
                if (!event.result && action == "EDIT") {
                    gardenLayoutView.removeView(collocatedImageView) // illegalException 방지용
                    previousCollocatedImage(false) // 기존에 있던 자리로 다시 세팅

                    // 초기화
                    inventoryData = null
                }
            }
        }
    }

    // 인벤토리에서 롱 클릭
    override fun onItemLongClickListener(view: View, pos: Int) {
        val inventoryItem = inventoryAdapter.getItem(pos)

        if (inventoryItem.category != Inventory.ITEM_CATEGORY_WALLPAPER
            && inventoryItem.itemCount > 0
        ) { // 아이템 개수가 0이 아닐 때
            inventoryData = inventoryItem
            action = "ADD" // 추가 액션

            startDrag(view) // 드래그 시작
        }
    }

    // 인벤토리 아이템 클릭
    override fun onItemClickListener(pos: Int) {
        val inventoryItem = inventoryAdapter.getItem(pos)

        if (inventoryItem.category == Inventory.ITEM_CATEGORY_WALLPAPER) {
            // 벽지를 클릭했을경우
            if (!inventoryItem.isUsing) {
                applyWallpaperDialog(inventoryItem)
            }
        } else {
            // 아이템을 클릭했을경우
            if (inventoryItem.itemCount != 0) {
                Toast.makeText(requireContext(), "아이템을 꾹 눌러보세요!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    // 배치된 아이템 롱 클릭
    private val collocatedItemLongClick = View.OnLongClickListener {
        val tagSplit = getTagSplit(it.tag.toString())
        val itemId = tagSplit[0]
        val category = tagSplit[1]
        val collocatedPos = tagSplit[2] // 태그에 저장시킨 배치된 뷰의 위치를 가져옴

        inventoryData = mViewModel.getItemWithIdAndTag(itemId, category)
        collocatedImageView = it as ImageView
        action = "EDIT" // 수정 액션

        startDrag(it) // 드래그 시작
        collocateAdapter.setCollocatedList(collocatedPos, false)
        gardenLayoutView.removeView(collocatedImageView) // 드래그 하고있는 뷰를 없앰

        true
    }

    // 인벤토리 드래그 리스너
    private val inventoryDragListener = View.OnDragListener { v, event ->
        if (action == "EDIT") {
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    true
                }
                DragEvent.ACTION_DROP -> {
                    // 이전에 있던 아이템을 인벤토리에 집어넣음
                    val notifyPos = mViewModel.putItem(inventoryData!!)
                    if (notifyPos != -1) {
                        // 집어넣는 아이템의 카테고리가 인벤토리 카테고리와 같을 경우
                        inventoryAdapter.notifyItemChanged(notifyPos)
                    }
                    previousCollocatedImage(true)

                    // 초기화
                    inventoryData = null
                    collocatedImageView = null
                    true
                }
                else -> false
            }
        } else {
            false
        }
    }

    // 옵저버 설정
    private fun setObserver() {
        // 로딩 여부
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                val uid = gViewModel.user.value!!.uid
                mViewModel.getInventoryFromServer(uid)
            }
        }
        // 인벤토리 확장 여부
        mViewModel.isExpand.observe(viewLifecycleOwner) { isExpand ->
            val state = motionLayoutView.currentState
            if (isExpand) {
                // 확장
                if (state == motionLayoutView.startState) {
                    motionLayoutView.transitionToEnd()
                }
            } else {
                // 축소
                if (state == motionLayoutView.endState) {
                    motionLayoutView.transitionToStart()
                }
            }
        }
        mViewModel.category.observe(viewLifecycleOwner) { category ->
            mViewModel.changeInventoryFilter(category) // 인벤토리 필터 바꾸기
        }
        // 이전에 저장된 정원 데이터 정보
        mViewModel.savedGardenList.observe(viewLifecycleOwner) { list ->
            if (isFirst) { // 처음 시작때만 읽어오게 하기
                list.forEach { garden ->
                    // 정원 아이템의 카테고리가 벽지가 아닐때
                    if (garden.category != Inventory.ITEM_CATEGORY_WALLPAPER) {
                        collocateImage(garden) // 이미지 배치
                        collocateAdapter.setCollocatedList(garden.collocatedPos, true)
                    } else { // 벽지일때
                        applyWallpaper(garden, false)
                    }
                }

                isFirst = false
            }
        }
        mViewModel.filterList.observe(viewLifecycleOwner) { list ->
            inventoryAdapter.syncData(list)
        }
        // 오류 여부
        mViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                Toast.makeText(requireContext(), ErrorMessage.CONNECT_ERROR, Toast.LENGTH_SHORT)
                    .show()
                mViewModel.isError.value = false
            }
        }
    }

    private fun showExitDialog() {
        with(MaterialAlertDialogBuilder(requireContext())) {
            setMessage("저장하지 않고 나가시겠습니까?")
            setPositiveButton("확인") { dialog, which ->
                findNavController().navigateUp()
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.cancel()
            }

            show()
        }
    }

    // 벽지 적용
    private fun applyWallpaperDialog(inventoryItem: Inventory) {
        with(MaterialAlertDialogBuilder(requireContext())) {
            setMessage("\"${inventoryItem.itemName}\"를 적용하겠습니까?")
            setPositiveButton("확인") { dialog, which ->
                val gardenData = Garden(
                    null,
                    inventoryItem.id,
                    inventoryItem.itemImage,
                    inventoryItem.category,
                    -1,
                    0f,
                    0f
                )
                applyWallpaper(gardenData, true) // 벽지 적용

                inventoryItem.isUsing = true // 현재 누른 벽지를 사용함
                mViewModel.isEdited.value = true
                inventoryAdapter.notifyDataSetChanged()
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.cancel()
            }

            show()
        }
    }

    private fun applyWallpaper(gardenData: Garden, isEdited: Boolean) {
        if (isEdited) {
            val gardenList = mViewModel.savedGardenList.value!!
            val gardenIdx = gardenList.indexOf(gardenData)

            if (gardenIdx != -1) { // 이전에 저장시킨 벽지데이터가 존재 할 때
                // 인벤토리 정보 수정
                val savedGardenData = gardenList[gardenIdx]
                mViewModel.getItemWithIdAndTag(savedGardenData.itemId, savedGardenData.category)
                    .isUsing = false

                gardenList[gardenIdx] = gardenData // 벽지 정보가 담긴 정원데이터 수정
            } else {
                gardenList.add(gardenData) // 벽지 정보가 담긴 정원데이터 추가
            }
        }

        mViewModel.wallpaperData.value = gardenData.itemImage // 벽지 이미지 변경
    }

    // 이미지 배치
    private fun collocateImage(gardenData: Garden) {
        val imageView = ImageView(requireContext()).apply {
            this.tag = "${gardenData.itemId}-${gardenData.category}-${gardenData.collocatedPos}"
            this.x = gardenData.collocatedX
            this.y = gardenData.collocatedY

            setOnLongClickListener(collocatedItemLongClick)
        }
        val params = LinearLayout.LayoutParams(
            (60 * resources.displayMetrics.density).toInt(),
            (60 * resources.displayMetrics.density).toInt()
        ).apply {
            setMargins((6 * resources.displayMetrics.density).toInt(), 0, 0, 0)
        }

        // byte -> bitmap
        val imageByte = Base64.decode(gardenData.itemImage, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(
            imageByte,
            0,
            imageByte.size
        )
        Glide.with(requireContext())
            .load(bitmap)
            .into(imageView)

        // 이미지뷰 추가
        gardenLayoutView.addView(imageView, params)
    }

    // 이전에 배치되어있던 뷰 액션
    private fun previousCollocatedImage(isEdited: Boolean) {
        val tagSplit = getTagSplit(collocatedImageView!!.tag.toString())
        val itemId = tagSplit[0]
        val category = tagSplit[1]
        val collocatedPos = tagSplit[2]

        if (isEdited) { // 이전에 있었던 자리에 대한 정보를 지움
            mViewModel.savedGardenList.value!!
                .remove(
                    Garden(0, itemId, "", category, collocatedPos, 0f, 0f)
                )
            mViewModel.isEdited.value = true

            collocateAdapter.setCollocatedList(collocatedPos, false)
        } else { // 이전에 있었던 자리에 다시 배치
            gardenLayoutView.addView(collocatedImageView)
            collocateAdapter.setCollocatedList(collocatedPos, true)
        }
    }

    private fun startDrag(view: View) {
        val dragItem = ClipData.Item(view.tag as CharSequence)
        val dragData = ClipData(
            view.tag as CharSequence,
            arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
            dragItem
        )

        // 드래그 시작
        view.startDragAndDrop(
            dragData,
            View.DragShadowBuilder(view),
            null,
            0
        )
    }

    private fun getTagSplit(tag: String): List<Int> {
        return tag.split("-")
            .map { it.toInt() }
    }

    // 뒤로가기 동작
    private val backPressed = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {
            val isEdited = mViewModel.isEdited.value!!
            if (isEdited) {
                showExitDialog()
            } else {
                findNavController().navigateUp()
            }
        }
    }
}