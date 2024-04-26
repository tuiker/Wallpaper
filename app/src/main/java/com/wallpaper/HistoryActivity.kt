package com.wallpaper

import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.dinuscxj.refresh.RecyclerRefreshLayout
import com.myretrofit2.bean.DataWallpaper
import com.wallpaper.adapter.HistoryWallpaperAdapter
import com.wallpaper.databinding.ActivityHistoryBinding
import com.wallpaper.myViewmodel.WallpaperHistoryViewModel
import com.wallpaper.mylibrary.BaseActivity
import com.wallpaper.mylibrary.utils.GetStatusBarHeight

class HistoryActivity : BaseActivity<ActivityHistoryBinding>() {
    private lateinit var viewModel: WallpaperHistoryViewModel
    private lateinit var mRecyclerHistory: RecyclerView
    private lateinit var mHistoryWallpaperAdapter: HistoryWallpaperAdapter
    private var page = 1
    private lateinit var recyclerRefreshLayout: RecyclerRefreshLayout
    private lateinit var lottie: LottieAnimationView
    override fun getView(): ActivityHistoryBinding {
        return ActivityHistoryBinding.inflate(layoutInflater)
    }

    override fun init() {
        GetStatusBarHeight.setBarHeight(vb.textView21, application, 0, 0, 0, 0)
        initView()
        initData()
        initEvent()
    }

    private fun initEvent() {
        recyclerRefreshLayout.setOnRefreshListener {
            page = 1
            viewModel.getHistoryList(page, 20, getToken())
        }
    }

    private fun initData() {
        mHistoryWallpaperAdapter = HistoryWallpaperAdapter()
        viewModel = ViewModelProvider(this)[WallpaperHistoryViewModel::class.java]
        viewModel.getHistoryList(page, 20, getToken())
        viewModel.mHistoryListAll.observe(this) {
            log("历史记录-->$it")
            if (it != null) {
                when (it.code) {
                    1000 -> {
                        recyclerRefreshLayout.setRefreshing(false)
                        if (page <= 1) {
                            mHistoryWallpaperAdapter.submitList(it.data.list)
                            if (it.data.list.isEmpty()) {
                                lottie.setAnimation(R.raw.animation_null)
                                lottie.repeatCount = 0
                            } else {
                                lottie.visibility = View.GONE
                            }
                        } else {
                            mHistoryWallpaperAdapter.addAll(it.data.list)
                        }

                    }

                    401 -> toPageFinish(LoginActivity::class.java)
                }
            } else {
                toastError()
            }
        }
        mRecyclerHistory.adapter = mHistoryWallpaperAdapter
    }

    private fun initView() {
        lottie = vb.lottie
        recyclerRefreshLayout = vb.recyclerRefreshLayout
        mRecyclerHistory = vb.recyclerHistory
        val staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        mRecyclerHistory.layoutManager = staggeredGridLayoutManager
        (mRecyclerHistory.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
        (mRecyclerHistory.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        mRecyclerHistory.itemAnimator?.changeDuration = 0
        mRecyclerHistory.setHasFixedSize(true)
    }

    fun back(view: View) {
        finish()
    }
}