package com.wallpaper.mini.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.QuickAdapterHelper
import com.chad.library.adapter4.loadState.LoadState
import com.chad.library.adapter4.loadState.trailing.TrailingLoadStateAdapter
import com.dinuscxj.refresh.RecyclerRefreshLayout
import com.wallpaper.mini.R
import com.wallpaper.mini.SettingWallpaperActivity
import com.wallpaper.mini.adapter.TypeLoadMoreAdapter
import com.wallpaper.mini.myViewmodel.WallpaperPageListViewModel
import com.wallpaper.mini.adapter.WallpaperTypeAdapter
import com.wallpaper.mini.databinding.FragmentWallpaperTypeBinding
import com.wallpaper.mylibrary.BaseFragment

class WallpaperTypeFragment : BaseFragment<FragmentWallpaperTypeBinding>() {
    private var position: Int = 0
    private lateinit var viewModel: WallpaperPageListViewModel
    private lateinit var mRecyclerWallpaperType: RecyclerView
    private lateinit var recyclerRefreshLayout: RecyclerRefreshLayout
    private var page = 1
    private lateinit var wallpaperTypeAdapter: WallpaperTypeAdapter
    private lateinit var helper: QuickAdapterHelper
    private lateinit var loadMoreAdapter: TypeLoadMoreAdapter

    override fun getViewBinding(): FragmentWallpaperTypeBinding {
        return FragmentWallpaperTypeBinding.inflate(layoutInflater)
    }

    override fun init() {
        initView()
        initData()
        initEvent()
    }

    private fun initEvent() {
    }

    private fun initView() {
        mRecyclerWallpaperType = vb.recyclerWallpaperType
        mRecyclerWallpaperType.layoutManager = LinearLayoutManager(application)
        recyclerRefreshLayout = vb.recyclerRefreshLayout
        recyclerRefreshLayout.setRefreshInitialOffset(-100f)
        recyclerRefreshLayout.setRefreshTargetOffset(300f)
        recyclerRefreshLayout.setAnimateToRefreshInterpolator(AccelerateInterpolator())
    }

    private fun initData() {
        arguments?.let {
            position = it.getInt("position", 0) // 读取传递过来的参数
        }
        setAdapter()
        viewModel = ViewModelProvider(this)[WallpaperPageListViewModel::class.java]
        viewModel.getCategoryListAll(page, position)
        viewModel.mCategoryListAll.observe(requireActivity()) {
            if (it != null && it.code == 1000) {
                log("壁纸分类-> $position,${it.data.list}")
                if (page <= 1) {
                    wallpaperTypeAdapter.submitList(it.data.list)
                    if (it.data.list.isEmpty()) {
                        vb.lottie.setAnimation(R.raw.animation_null)
                    } else {
                        vb.lottie.visibility = View.GONE
                    }
                } else {
                    wallpaperTypeAdapter.addAll(it.data.list)
                }
                if (it.data.list.size >= 10) {
                    helper.trailingLoadState = LoadState.NotLoading(false)
                }
                if (it.data.list.size in 3..9) {
                    helper.trailingLoadState = LoadState.NotLoading(true)
                }
                page++
            } else {
                vb.lottie.setAnimation(R.raw.animation_404)
            }
            recyclerRefreshLayout.setRefreshing(false)
            loadMoreAdapter.checkDisableLoadMoreIfNotFullPage()
        }
        recyclerRefreshLayout.setOnRefreshListener {
            setAdapter()
            page = 1
            viewModel.getCategoryListAll(page, position)
        }
    }

    private fun setAdapter() {
        wallpaperTypeAdapter = WallpaperTypeAdapter(
            object : WallpaperTypeAdapter.ItemClickListener {
                override fun onItemClicked(id: Int) {
                    startActivity(
                        Intent(
                            context,
                            SettingWallpaperActivity::class.java
                        ).putExtra("wallpaperId", id)
                    )
                }
            },
        )
        loadMoreAdapter = TypeLoadMoreAdapter(application)
        loadMoreAdapter.setOnLoadMoreListener(object : TrailingLoadStateAdapter.OnTrailingListener {
            override fun onLoad() {
                // 执行加载更多的操作，通常都是网络请求
                viewModel.getCategoryListAll(page, position)
            }

            override fun onFailRetry() {
                // 加载失败后，点击重试的操作，通常都是网络请求
                toastError()
            }

            override fun isAllowLoading(): Boolean {
                // 是否允许触发“加载更多”，通常情况下，下拉刷新的时候不允许进行加载更多
                return recyclerRefreshLayout.isShown
            }
        })
        helper = QuickAdapterHelper.Builder(wallpaperTypeAdapter)
            .setTrailingLoadStateAdapter(loadMoreAdapter) // 传递自定义的“加载跟多”
            .build()
        mRecyclerWallpaperType.adapter = helper.adapter
    }

    companion object {
        fun newInstance(position: Int): WallpaperTypeFragment {
            val fragment = WallpaperTypeFragment()
            // 可以传递一些参数到 Fragment
            val args = Bundle()
            args.putInt("position", position)
            fragment.arguments = args
            return fragment
        }
    }
}