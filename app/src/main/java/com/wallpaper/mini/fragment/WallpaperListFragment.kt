package com.wallpaper.mini.fragment

import android.content.Intent
import android.net.Uri

import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.chad.library.adapter4.QuickAdapterHelper
import com.chad.library.adapter4.loadState.LoadState
import com.chad.library.adapter4.loadState.trailing.TrailingLoadStateAdapter
import com.dinuscxj.refresh.RecyclerRefreshLayout
import com.myretrofit2.RetrofitUtil
import com.myretrofit2.api.AppUrl
import com.myretrofit2.bean.AdRecordRequestData
import com.myretrofit2.bean.ListData
import com.myretrofit2.bean.SuccessBean
import com.wallpaper.mini.FindWallpaperActivity
import com.wallpaper.mini.R
import com.wallpaper.mini.adapter.WallpaperAdapter
import com.wallpaper.mini.myViewmodel.WallpaperHomeViewModel
import com.wallpaper.mini.adapter.WallpaperLoadMoreAdapter
import com.wallpaper.mini.databinding.FragmentWallpaperListBinding
import com.wallpaper.mylibrary.BaseFragment
import com.wallpaper.mylibrary.utils.GetStatusBarHeight.Companion.setBarHeight
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WallpaperListFragment : BaseFragment<FragmentWallpaperListBinding>() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mFindWallpaper: TextView
    private lateinit var viewModel: WallpaperHomeViewModel
    private lateinit var swipeRefreshLayout: RecyclerRefreshLayout
    private lateinit var wallpaperAdapter: WallpaperAdapter
    private var page = 1
    private var pageSize = 10
    private lateinit var helper: QuickAdapterHelper
    private lateinit var gridLayoutManager: StaggeredGridLayoutManager
    override fun getViewBinding(): FragmentWallpaperListBinding {
        return FragmentWallpaperListBinding.inflate(layoutInflater)
    }

    override fun init() {
        //20faa2ddde754f92a55fd57508729aee
        initView()
        initData()
        initEvent()
    }

    private fun initView() {
        mFindWallpaper = vb.findWallpaper
        mRecyclerView = vb.wallpaperList
        swipeRefreshLayout = vb.swipeRefreshLayout
        swipeRefreshLayout.setRefreshInitialOffset(-200f)
        swipeRefreshLayout.setRefreshTargetOffset(300f)
        swipeRefreshLayout.setAnimateToRefreshInterpolator(AccelerateInterpolator())
    }

    private fun initData() {
        viewModel = ViewModelProvider(requireActivity())[WallpaperHomeViewModel::class.java]
        viewModel.getWallpaperHomeListAll(page, pageSize)
        setAdapter()
        gridLayoutManager = StaggeredGridLayoutManager(2, VERTICAL)
        mRecyclerView.layoutManager = gridLayoutManager
        mRecyclerView.setItemViewCacheSize(8)

        viewModel.mHomeListAll.observe(requireActivity()) {
            log("首页-->$it")
            if (it != null && it.code == 1000) {
                log("${it.data}")
                vb.lottie.visibility = View.GONE
                if (page <= 1) {
                    wallpaperAdapter.submitList(it.data.list)
                    if (it.data.list.isEmpty()) {
                        vb.lottie.setAnimation(R.raw.animation_null)
                    }
                } else {
                    wallpaperAdapter.addAll(it.data.list)
                }
                if (it.data.list.size >= pageSize) {
                    helper.trailingLoadState = LoadState.NotLoading(false)
                } else {
                    helper.trailingLoadState = LoadState.NotLoading(true)
                }
                page++
            } else {
                vb.lottie.setAnimation(R.raw.animation_404)
            }
            swipeRefreshLayout.setRefreshing(false)
        }
        //刷新
        swipeRefreshLayout.setOnRefreshListener {
            setAdapter()
            page = 1
            viewModel.getWallpaperHomeListAll(page, pageSize)
        }
    }

    private fun setAdapter() {
        wallpaperAdapter = WallpaperAdapter(object : WallpaperAdapter.ItemClickListener {
            override fun onItemClicked(listData: ListData) {
                log("${listData.advUrl.toString().trim()}")
                if (listData.advUrl.toString().startsWith("http")||listData.advUrl.toString().startsWith("https")) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(listData.advUrl.toString().trim())
                        )
                    )
                    //点击广告埋点
                    ad(listData)
                }else{
                    toast(getString(R.string.not_ad))
                }
            }
        })
        // 实例化自定义"加载更多"的类
        val loadMoreAdapter = WallpaperLoadMoreAdapter(application)
        loadMoreAdapter.setOnLoadMoreListener(object : TrailingLoadStateAdapter.OnTrailingListener {
            override fun onLoad() {
                // 执行加载更多的操作，通常都是网络请求
                log("page-->$page")
                viewModel.getWallpaperHomeListAll(page)
            }

            override fun onFailRetry() {
                // 加载失败后，点击重试的操作，通常都是网络请求
                toastError()
            }

            override fun isAllowLoading(): Boolean {
                // 是否允许触发“加载更多”，通常情况下，下拉刷新的时候不允许进行加载更多
                return swipeRefreshLayout.isShown
            }
        })
        helper = QuickAdapterHelper.Builder(wallpaperAdapter)
            .setTrailingLoadStateAdapter(loadMoreAdapter) // 传递自定义的“加载跟多”
            .build()

        mRecyclerView.adapter = helper.adapter
    }

    private fun initEvent() {
        setBarHeight(mFindWallpaper, application, 32, 12, 32, 0)
        mFindWallpaper.setOnClickListener {
            toPage(FindWallpaperActivity::class.java)
        }
    }

    private fun ad(listData: ListData) {
        val apiPostService = RetrofitUtil.getPostCall(getToken())
        val wallpaperHomeListCall =
            apiPostService.adRecord(
                AppUrl.clickAdRecord,
                AdRecordRequestData(
                    listData.id,
                    listData.contentsType.toString().toDouble().toInt()
                )
            )
        wallpaperHomeListCall?.enqueue(object : Callback<SuccessBean?> {
            override fun onResponse(
                call: Call<SuccessBean?>,
                response: Response<SuccessBean?>
            ) {
                log("${response.code()}")
                if (response.isSuccessful && response.code() == 200) {
                    log("点击广告")
                }
            }

            override fun onFailure(call: Call<SuccessBean?>, t: Throwable) {
                log("${t.message}")
            }
        })
    }
}