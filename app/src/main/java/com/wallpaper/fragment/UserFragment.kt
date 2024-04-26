package com.wallpaper.fragment

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.wallpaper.HistoryActivity
import com.wallpaper.LoginActivity
import com.wallpaper.R
import com.wallpaper.UserEditActivity
import com.wallpaper.UserFeedbackActivity
import com.wallpaper.adapter.MyWallpaperAdapter
import com.wallpaper.mylibrary.BaseFragment
import com.wallpaper.databinding.FragmentUserBinding
import com.wallpaper.myViewmodel.MyCollectViewModel
import com.wallpaper.myViewmodel.UserInfoViewModel
import com.wallpaper.mylibrary.utils.MyGlide

class UserFragment : BaseFragment<FragmentUserBinding>() {
    private lateinit var viewModel: UserInfoViewModel
    private lateinit var mCollectListViewModel: MyCollectViewModel
    private lateinit var mMyWallpaperList: RecyclerView
    private var page = 1
    override fun getViewBinding(): FragmentUserBinding {
        return FragmentUserBinding.inflate(layoutInflater)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserInfo(getToken())
        mCollectListViewModel.getMyCollect(page, 16, getToken())
    }

    override fun init() {
        initView()
        initData()
        initEvent()
    }

    private fun initData() {
        viewModel = ViewModelProvider(this)[UserInfoViewModel::class.java]
        viewModel.getUserInfo(getToken())
        viewModel.mUserInfo.observe(this) {
            log("用户信息--> $it")
            if (it != null) {
                when (it.code) {
                    1000 -> {
                        vb.tvUsername.text = it.data.nickname
                        vb.tvSign.text = it.data.signature
                        if (it.data.headImg != null) {
                            MyGlide.setGlideImage(
                                application,
                                it.data.headImg,
                                vb.ivUserAvatar,
                                R.drawable.set_avatar,
                                200
                            )
                        }
                    }

                    401 -> toPageFinish(LoginActivity::class.java)
                }
            } else {
                toastError()
            }
        }
        mCollectListViewModel = ViewModelProvider(this)[MyCollectViewModel::class.java]
        mCollectListViewModel.getMyCollect(page, 16, getToken())
        val myWallpaperAdapter = MyWallpaperAdapter()
        mCollectListViewModel.mMyCollectList.observe(requireActivity()) {
            log("收藏-->${it}")
            if (it != null) {
                when (it.code) {
                    1000 -> {
                        if (page == 1) {
                            if (it.data.list.isEmpty()){
                                vb.lottie.setAnimation(R.raw.animation_null)
                            }else{
                                vb.lottie.visibility=View.GONE
                            }
                            myWallpaperAdapter.submitList(it.data.list)
                        } else {
                            myWallpaperAdapter.addAll(it.data.list)
                        }
                    }
                    401 -> toPageFinish(LoginActivity::class.java)
                }
            } else {
                toastError()
            }

        }
        mMyWallpaperList.adapter = myWallpaperAdapter
    }

    private fun initEvent() {
        vb.ivEdit.setOnClickListener {
            toPage(UserEditActivity::class.java)
        }
        vb.ivUserAvatar.setOnClickListener {
            toPage(UserEditActivity::class.java)
        }
        //历史记录
        vb.ivDownloadHistory.setOnClickListener {
            toPage(HistoryActivity::class.java)
        }
        //反馈
        vb.ivUserFeedback.setOnClickListener {
            toPage(UserFeedbackActivity::class.java)
        }
    }

    private fun initView() {
        mMyWallpaperList = vb.myWallpaperList
        mMyWallpaperList.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }
}