package com.sg.android.bambooflower.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.ImagePagerAdapter
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.databinding.FragmentPostBinding
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.ui.SecondActivity
import com.sg.android.bambooflower.viewmodel.GlobalViewModel
import com.sg.android.bambooflower.viewmodel.postFragment.PostViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostFragment : Fragment(), View.OnClickListener {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<PostViewModel>()
    private val dots = mutableListOf<ImageView>()

    private lateinit var imageAdapter: ImagePagerAdapter
    private lateinit var postData: Post
    private lateinit var userData: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentPostBinding.inflate(inflater)
        postData = gViewModel.post.value!!
        userData = gViewModel.user.value!!
        imageAdapter = ImagePagerAdapter(postData.image!!)
        mViewModel.isCheerUp.value = postData.favorites.contains(userData.uid)

        Log.i("Check", "data: $postData")

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.post = postData
            this.user = userData
            this.clickListener = this@PostFragment
            imagePager.adapter = imageAdapter

            setDots(imagePager, sliderDots)

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        isExistPost() // 게시글 존재 여부
        setObserver()
    }

    override fun onStart() {
        super.onStart()
        // 툴바 설정
        with((activity as MainActivity).supportActionBar) {
            this?.show()
            this?.title = ""
            this?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onDestroyView() {
        gViewModel.post.value = null
        super.onDestroyView()
    }

    // 메뉴 설정
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_post_fragment, menu)

        if (postData.uid == userData.uid || userData.uid == "DQ09dobyPqY0SrLHIMcRYmdOdfO2") {
            // 내 게시글일 경우 삭제하기만 표시
            menu.getItem(1).isVisible = false
        } else {
            // 내 게시글이 아닐 경우 신고하기만 표시
            menu.getItem(0).isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            R.id.menu_delete_post -> { // 삭제하기
                deletePost()
                true
            }
            R.id.menu_report_post -> { // 신고하기
                reportPost()
                true
            }
            else -> false
        }
    }

    // 버튼 액션
    override fun onClick(v: View) {
        when (v.id) {
            R.id.fullscreen_btn -> {
                showImage()
            }
            R.id.cheer_up_btn -> {
                cheerUpAction()
            }
            else -> {
            }
        }
    }

    private fun setObserver() {
        // 삭제 완료 확인
        mViewModel.isDeleted.observe(viewLifecycleOwner) {
            if (it) {
                gViewModel.syncData.value = true

                findNavController().navigateUp()
            }
        }
        // 서버 오류
        mViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                Toast.makeText(requireContext(), "서버와 연결 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT)
                    .show()
                mViewModel.isError.value = false
            }
        }
        mViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                (requireActivity() as MainActivity).loading()
            } else {
                (requireActivity() as MainActivity).ready()
            }
        }
        // 이미지 위치
        mViewModel.imagePos.observe(viewLifecycleOwner) { pos ->
            for (i in postData.image!!.indices) {
                dots[i].setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.non_active_dot_shape
                    )
                )
            }

            dots[pos].setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.active_image_dot_shape
                )
            )
        }
    }

    // 이미지를 크게 보여주는 화면으로 이동함
    private fun showImage() {
        val intent = Intent(requireContext(), SecondActivity::class.java).apply {
            putExtra(Contents.EXTRA_SET_IMAGE, postData.image!!.toTypedArray())
            putExtra(Contents.EXTRA_SET_POS, mViewModel.imagePos.value!!)
            action = Contents.SHOW_IMAGE_FRAG
        }
        startActivity(intent)
    }

    // 성공 버튼 액션
    private fun cheerUpAction() {
        if (userData.uid == postData.uid) {
            showAccept()
        } else {
            acceptMission()
        }
    }

    private fun showAccept() {
        findNavController().navigate(R.id.acceptListDialog)
    }

    // 미션 인정
    private fun acceptMission() {
        with(MaterialAlertDialogBuilder(requireContext())) {
            setMessage("성공으로 인정하시겠습니까?")
            setPositiveButton("확인") { dialog, which ->
                if (!mViewModel.isCheerUp.value!!) {
                    mViewModel.cheerUp(userData, postData)
                } else {
                    Toast.makeText(requireContext(), "이미 인정한 게시글입니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            show()
        }
    }

    // 페이저 위치를 표시하는 뷰
    private fun setDots(pager: ViewPager2, sliderDots: LinearLayout) {
        for (i in postData.image!!.indices) {
            dots.add(ImageView(requireContext()))
            dots[i].setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.non_active_dot_shape
                )
            )

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 0, 8, 0)
            }

            sliderDots.addView(dots[i], params)
        }

        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mViewModel.imagePos.value = position
            }
        })
    }

    // 게시글 존재 여부
    private fun isExistPost() {
        mViewModel.getPostData(postData.postPath!!)
            .addOnSuccessListener {
                if (it.toObject(Post::class.java) == null) {
                    // 게시글 존재 X
                    with(MaterialAlertDialogBuilder(requireContext())) {
                        setMessage("해당 게시글이 존재하지 않습니다.")
                        setPositiveButton("확인") { dialog, which ->
                            findNavController().navigateUp()
                        }
                        setCancelable(false)

                        show()
                    }
                }
            }
    }

    // 게시글 삭제
    private fun deletePost() {
        with(MaterialAlertDialogBuilder(requireContext())) {
            setMessage("정말로 삭제하시겠습니까?")
            setPositiveButton("확인") { dialog, which ->
                mViewModel.deletePost(postData)
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            show()
        }
    }

    private fun reportPost() {
        findNavController().navigate(R.id.reportDialog)
    }
}