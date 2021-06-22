package com.sg.android.bambooflower.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.AlbumAdapter
import com.sg.android.bambooflower.data.Album
import com.sg.android.bambooflower.databinding.FragmentAlbumBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.ui.SecondActivity
import com.sg.android.bambooflower.ui.view.AlbumItemDecoration
import com.sg.android.bambooflower.viewmodel.albumFragment.AlbumViewModel

class AlbumFragment : Fragment(), AlbumAdapter.AlbumItemListener {
    private val mViewModel by viewModels<AlbumViewModel>()

    private lateinit var albumAdapter: AlbumAdapter
    private var selectMenuItem: MenuItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentAlbumBinding.inflate(inflater)
        val intentData =
            requireActivity().intent.getStringArrayExtra(Contents.EXTRA_SET_IMAGE) // 이전에 선택했던 이미지를 가져옴
        albumAdapter = AlbumAdapter().apply {
            setOnAlbumItemListener(this@AlbumFragment)
        }
        mViewModel.selectedImage.value = intentData!!.toMutableList()

        // 바인딩 설정
        with(binding) {
            with(imageList) {
                adapter = albumAdapter
                addItemDecoration(AlbumItemDecoration(requireContext()))
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        getAllImage() // 이미지를 모두 가져옴
        setObserver()
    }

    override fun onStart() {
        super.onStart()
        // 툴바 설정
        with((activity as SecondActivity).supportActionBar) {
            this?.show()
            this?.title = "0개 선택됨"
            this?.setDisplayHomeAsUpEnabled(true)
        }
    }

    // 사진 선택
    override fun onItemClickListener(pos: Int) {
        val albumData = albumAdapter.getItem(pos)
        val selectedImage = mViewModel.selectedImage.value!!

        if (selectedImage.size < 5 || albumData.isChecked) { // 5개 이하로 선택하게 함
            albumData.isChecked = !albumData.isChecked
            albumAdapter.notifyItemChanged(pos)

            if (albumData.isChecked) {
                mViewModel.addImage(albumData.imageUri.toString())
            } else {
                mViewModel.removeImage(albumData.imageUri.toString())
            }
        } else {
            Toast.makeText(requireContext(), "5개 이하로 선택해주세요.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // 메뉴 설정
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_album_fragment, menu)

        selectMenuItem = menu.getItem(0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().finish()
                true
            }
            R.id.menu_select -> { // 사진 선택
                val intent = Intent().apply {
                    val extra = mViewModel.selectedImage.value!!.toTypedArray()
                    putExtra(Contents.EXTRA_GET_IMAGE, extra)
                }

                with(requireActivity()) {
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                true
            }
            else -> false
        }
    }

    // 이미지를 가져옴
    private fun getAllImage() {
        val cursor = requireContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID),
            null,
            null,
            MediaStore.MediaColumns.DATE_ADDED + " desc"
        )

        mViewModel.getAllImage(cursor!!)
    }

    // 옵저버 설정
    private fun setObserver() {
        // 모든 이미지
        mViewModel.album.observe(viewLifecycleOwner) {
            albumAdapter.syncData(it)
        }
        // 선택된 이미지
        mViewModel.selectedImage.observe(viewLifecycleOwner) {
            selectMenuItem?.isEnabled = it.size != 0
            (activity as SecondActivity).supportActionBar?.title = "${it.size}개 선택됨"
        }
    }
}