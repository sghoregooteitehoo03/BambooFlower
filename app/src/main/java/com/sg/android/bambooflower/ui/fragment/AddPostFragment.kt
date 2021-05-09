package com.sg.android.bambooflower.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.ImageAdapter
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.databinding.FragmentAddPostBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.ui.SecondActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.addPostFragment.AddPostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class AddPostFragment : Fragment(), ImageAdapter.ImageItemListener, View.OnClickListener {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<AddPostViewModel>()
    private var imageList: MutableList<Uri> = mutableListOf() // 선택한 이미지를 담을 리스트

    private lateinit var imageAdapter: ImageAdapter
    private lateinit var imm: InputMethodManager
    private lateinit var user: User
    private val simpleCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP
                or ItemTouchHelper.DOWN
                or ItemTouchHelper.START
                or ItemTouchHelper.END,
        0
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.bindingAdapterPosition // 이전에 있던 위치
            val toPosition = target.bindingAdapterPosition // 바뀔 위치

            // 리스트의 위치를 바꿈
            Collections.swap(imageList, fromPosition, toPosition)
            imageAdapter.notifyItemMoved(fromPosition, toPosition)

            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            TODO("Not yet implemented")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        val binding = FragmentAddPostBinding.inflate(inflater)
        imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imageAdapter = ImageAdapter().apply {
            setOnImageItemListener(this@AddPostFragment)
        }
        user = gViewModel.user.value!!
        mViewModel.title.value = "[${gViewModel.user.value?.myMission}]"

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.user = user
            this.clickListener = this@AddPostFragment

            with(imageList) {
                adapter = imageAdapter
                itemTouchHelper.attachToRecyclerView(this)
            }

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        setObserver()
    }

    override fun onStart() {
        super.onStart()

        // 툴바 설정
        with((activity as MainActivity).supportActionBar) {
            this?.title = "인증하기"
            this?.setDisplayHomeAsUpEnabled(true)
            this?.show()
        }
    }

    // 메뉴 설정
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_addpost_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            R.id.menu_add_post -> { // 게시 버튼
                CoroutineScope(Dispatchers.IO).launch {
                    mViewModel.addPost(user, imageList, requireContext().contentResolver)
                }
                true
            }
            else -> false
        }
    }

    // 이미지 제거
    override fun onRemoveListener(pos: Int) {
        imageList.removeAt(pos)
        imageAdapter.notifyItemRemoved(pos)
    }

    // 선택한 이미지를 받아 옴
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Contents.GET_IMAGE && resultCode == Activity.RESULT_OK) {
            val selectedImageList =
                data?.getStringArrayExtra(Contents.EXTRA_GET_IMAGE)?.map {
                    it.toUri()
                }?.toMutableList() // 이미지 선택 화면에서 선택한 이미지를 가져옴
            imageList = selectedImageList!!

            imageAdapter.syncData(imageList) // 리스트 갱신
        }
    }

    // 권한 설정
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Contents.PERMISSION_CODE -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Toast.makeText(requireContext(), "권한을 허용해주세요.", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        getImage()
                    }
                } else {
                    if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        || checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    ) {
                        Toast.makeText(requireContext(), "권한을 허용해주세요.", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        getImage()
                    }
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    // 버튼 액션
    override fun onClick(v: View) {
        when (v.id) {
            R.id.get_image_btn -> {
                getImage()
            }
            else -> {
            }
        }
    }

    // 옵저버 설정
    private fun setObserver() {
        // 게시글 작성 성공 여부
        mViewModel.isSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                gViewModel.user.value = user
                gViewModel.syncData.value = true

                findNavController().navigateUp()
            }
        }
        // 로딩 화면
        mViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                (requireActivity() as MainActivity).loading()
            } else {
                (requireActivity() as MainActivity).ready()
            }
        }
        // 오류메세지
        mViewModel.errorMsg.observe(viewLifecycleOwner) { msg ->
            if (msg.isNotEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
                    .show()
                mViewModel.errorMsg.value = ""
            }
        }
    }

    private fun getImage() { // 이미지를 가져오는 화면으로 이동함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) { // 권한 설정
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Contents.PERMISSION_CODE
                )
            } else {
                getImageIntent()
            }
        } else {
            if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                || checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    Contents.PERMISSION_CODE
                )
            } else {
                getImageIntent()
            }
        }
    }

    private fun getImageIntent() {
        val intent = Intent(requireContext(), SecondActivity::class.java).apply {
            putExtra(
                Contents.EXTRA_SET_IMAGE,
                imageList.map { it.toString() }.toTypedArray()
            )
            action = Contents.SHOW_ALBUM_FRAG
        }
        startActivityForResult(intent, Contents.GET_IMAGE)
    }

    private fun checkPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) != PackageManager.PERMISSION_GRANTED
}