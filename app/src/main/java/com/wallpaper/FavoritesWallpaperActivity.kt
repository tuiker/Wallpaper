package com.wallpaper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.dinuscxj.refresh.RecyclerRefreshLayout
import com.myretrofit2.bean.ListData
import com.wallpaper.adapter.FavoritesWallpaperAdapter
import com.wallpaper.databinding.ActivityFavoritesWallpaperBinding
import com.wallpaper.myViewmodel.FavoritesListViewModel
import com.wallpaper.mylibrary.BaseActivity
import com.wallpaper.mylibrary.utils.GetStatusBarHeight
import kotlin.properties.Delegates

class FavoritesWallpaperActivity : BaseActivity<ActivityFavoritesWallpaperBinding>() {
    private lateinit var viewModel: FavoritesListViewModel
    private var page = 1
    private lateinit var lottie: LottieAnimationView
    private lateinit var recyclerFavoritesList: RecyclerView
    private lateinit var recyclerRefreshLayout: RecyclerRefreshLayout
    private var favoritesWallpaper by Delegates.notNull<Int>()
    private lateinit var mFavoritesWallpaperAdapter: FavoritesWallpaperAdapter
    private var list= mutableListOf<ListData>()
    override fun getView(): ActivityFavoritesWallpaperBinding {
        return ActivityFavoritesWallpaperBinding.inflate(layoutInflater)
    }

    override fun init() {
        GetStatusBarHeight.setBarHeight(vb.textView21, application, 0, 0, 0, 0)
        initView()
        initData()
        initEvent()
    }

    private fun initEvent() {
        recyclerRefreshLayout.setOnRefreshListener {
//            if (!recyclerFavoritesList.isComputingLayout && !recyclerFavoritesList.isAnimating) {
//                // 执行下拉刷新操作
//                recyclerRefreshLayout.setRefreshing(true)
//                // 其他刷新逻辑
//            }
            setAdapter()
            page = 1
            viewModel.getFavoritesList(page, 16, favoritesWallpaper, getToken())
        }
    }

    private fun initData() {
        favoritesWallpaper = intent.getIntExtra("favoritesWallpaper", 0)
        log("$favoritesWallpaper")
        setAdapter()
        viewModel = ViewModelProvider(this)[FavoritesListViewModel::class.java]
        viewModel.getFavoritesList(page, 16, favoritesWallpaper, getToken())
        viewModel.mFavoritesList.observe(this) {
            log("$it")
            if (it != null) {
                when (it.code) {
                    1000 -> {
                        if (page <= 1) {
                            log("${it.data.list.size}")
                            mFavoritesWallpaperAdapter.submitList(it.data.list)
                        } else {
                            mFavoritesWallpaperAdapter.addAll(it.data.list)
                        }
                        lottie.visibility = View.GONE
                    }

                    401 -> toPageFinish(LoginActivity::class.java)
                }
                recyclerRefreshLayout.setRefreshing(false)
            } else {
                toastError()
            }
        }
    }

    private fun initView() {
        lottie = vb.lottie
        recyclerFavoritesList = vb.recyclerFavoritesList

        val staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerFavoritesList.layoutManager = staggeredGridLayoutManager

        recyclerRefreshLayout = vb.recyclerRefreshLayout
        recyclerRefreshLayout.setRefreshInitialOffset(-200f)
        recyclerRefreshLayout.setRefreshTargetOffset(300f)
        recyclerRefreshLayout.setAnimateToRefreshInterpolator(AccelerateInterpolator())
    }

    private fun setAdapter() {
        mFavoritesWallpaperAdapter = FavoritesWallpaperAdapter()
//        mFavoritesWallpaperAdapter.isStateViewEnable=true
//        mFavoritesWallpaperAdapter.setStateViewLayout(application,R.layout.favorites_wallpaper_item)
        recyclerFavoritesList.adapter = mFavoritesWallpaperAdapter
    }

    fun back(view: View) {
        finish()
    }
}