package com.wallpaper.mini

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.room.Room
import com.google.android.flexbox.FlexboxLayout
import com.myretrofit2.RetrofitUtil
import com.myretrofit2.api.AppUrl
import com.myretrofit2.bean.AdRecordRequestData
import com.myretrofit2.bean.ListData
import com.myretrofit2.bean.SaveSearchRequestData
import com.myretrofit2.bean.SuccessBean
import com.wallpaper.mini.adapter.SearchHotAdapter
import com.wallpaper.mini.adapter.WallpaperAdapter
import com.wallpaper.mini.databinding.ActivityFindWllapaperBinding
import com.wallpaper.mini.myView.FlexBoxLayoutMaxLines
import com.wallpaper.mini.myViewmodel.HotSearchViewModel
import com.wallpaper.mini.myViewmodel.WallpaperHomeViewModel
import com.wallpaper.mylibrary.BaseActivity
import com.wallpaper.mylibrary.utils.GetStatusBarHeight.Companion.setBarHeight
import com.wallpaper.mylibrary.utils.StringToEditable
import com.wallpaper.mini.room.AppDatabase
import com.wallpaper.mini.room.SearchHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FindWallpaperActivity : BaseActivity<ActivityFindWllapaperBinding>() {
    private lateinit var mEdFindWallpaper: EditText
    private lateinit var mRecyclerHotFind: RecyclerView
    private lateinit var mRecyclerWallpaperSearch: RecyclerView
    private lateinit var db: AppDatabase
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var viewModel: WallpaperHomeViewModel
    private lateinit var hotSearchViewModel: HotSearchViewModel
    private lateinit var mFlexboxLayout: FlexBoxLayoutMaxLines
    private lateinit var wallpaperAdapter: WallpaperAdapter
    private var searchTextList = mutableListOf<String>()
    override fun getView(): ActivityFindWllapaperBinding {
        return ActivityFindWllapaperBinding.inflate(layoutInflater)
    }

    override fun init() {
        db = Room.databaseBuilder(
            application,
            AppDatabase::class.java, "search-database"
        ).build()
        coroutineScope = CoroutineScope(Dispatchers.IO)
        initView()
        initData()
        initEvent()
    }

    private fun initData() {
        //搜索壁纸
        viewModel = ViewModelProvider(this)[WallpaperHomeViewModel::class.java]
        viewModel.mHomeListAll.observe(this) {
            log("$it")
            if (it != null) {
                when (it.code) {
                    1000 -> {
                        wallpaperAdapter.submitList(it.data.list)
                        if (it.data.list.isEmpty()) {
                            vb.llResult.visibility = View.VISIBLE
                        } else {
                            vb.llResult.visibility = View.GONE
                        }
                    }
                }
            }
        }
        //热搜关键词
        hotSearchViewModel = ViewModelProvider(this)[HotSearchViewModel::class.java]
        hotSearchViewModel.getHotSearchList(getToken())
        hotSearchViewModel.mHotSearch.observe(this) {
            log("$it")
            if (it != null) {
                when (it.code) {
                    1000 -> {
                        mRecyclerHotFind.adapter =
                            SearchHotAdapter(
                                it,
                                object : SearchHotAdapter.AdapterItemClickListener {
                                    override fun onItemClicked(position: Int) {
                                        editSearch(it.data[position].keyword)
                                    }
                                })
                    }
                }
            } else {
                vb.imageView9.visibility = View.GONE
            }
        }
    }

    private fun initView() {
        mEdFindWallpaper = vb.findWallpaper
        mEdFindWallpaper.setHorizontallyScrolling(false)
        setBarHeight(mEdFindWallpaper, application, 0, 12, 0, 0)

        //搜索记录
        mFlexboxLayout = vb.flexboxLayout
        mFlexboxLayout.maxLine = 3
        val searchHistoryLiveData = db.searchHistoryDao().getAllSearchHistory()
        searchHistoryLiveData.observe(this) { searchHistoryList ->
            log("搜索记录-->$searchHistoryList")
            if (searchHistoryList.isEmpty()) {
                vb.tvRecentlySearch.visibility = View.GONE
                vb.tvClearRecord.visibility = View.GONE
                mFlexboxLayout.visibility = View.GONE
            } else {
                vb.tvRecentlySearch.visibility = View.VISIBLE
                vb.tvClearRecord.visibility = View.VISIBLE
                mFlexboxLayout.visibility = View.VISIBLE
            }
            // 更新用户界面显示搜索历史记录
            mFlexboxLayout.removeAllViews()
            searchTextList.clear()
            searchHistoryList.forEach { i ->
                if (!searchTextList.contains(i.searchTerm)) {
                    searchTextList.add(i.searchTerm)
                }
            }
            searchTextList.reversed().forEach { text ->
                if (text.isNotEmpty()) {
                    addFlexboxItem(mFlexboxLayout, text)
                }
            }
        }

        //热门搜索
        mRecyclerHotFind = vb.recyclerHotFind
        mRecyclerHotFind.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        //搜索壁纸列表
        mRecyclerWallpaperSearch = vb.recyclerWallpaperSearch
        mRecyclerWallpaperSearch.layoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
    }

    //添加记录
    private fun addFlexboxItem(flexboxLayout: FlexBoxLayoutMaxLines, text: String) {
        val textView = TextView(this)
        textView.text = text
        textView.textSize = 14F
        textView.setBackgroundResource(R.drawable.history_text_item_border) // 设置项的背景色
        textView.setTextColor(Color.parseColor("#808080"))
        textView.setPadding(32, 12, 32, 12)
        val layoutParams = FlexboxLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 32) // 设置项之间的间距
        textView.layoutParams = layoutParams
        //点击记录搜索
        textView.setOnClickListener {
            editSearch(text)
        }
        flexboxLayout.addView(textView)
    }

    //点击记录搜索
    private fun editSearch(text: String) {
        mEdFindWallpaper.text = StringToEditable.stringToEditable(text)
        coroutineScope.launch {
            deleteSearchTerm(text)
        }
        startSearch(text)
    }

    private fun initEvent() {
        vb.ivBack.setOnClickListener {
            finish()
        }
        //清除
        vb.ivClose.setOnClickListener {
            mEdFindWallpaper.text.clear()
            mRecyclerWallpaperSearch.visibility = View.GONE
            vb.llResult.visibility = View.GONE
        }
        // 清除搜索历史记录
        vb.tvClearRecord.setOnClickListener {
            coroutineScope.launch {
                // 清除搜索历史记录
                clearSearchHistory()
            }
        }
        vb.imageView8.setOnClickListener {
            startSearch(mEdFindWallpaper.text.toString())
        }
        // 监听软键盘上的确定按钮点击事件
        mEdFindWallpaper.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                //隐藏软键盘
                hideKeyboard()
                startSearch(textView.text.toString())
                log("搜索")
                true
            } else {
                false
            }
        }
    }

    private fun startSearch(text: String) {
        wallpaperAdapter = WallpaperAdapter(object : WallpaperAdapter.ItemClickListener {
            override fun onItemClicked(listData: ListData) {
                log("${listData.advUrl.toString().trim()}")
                if (listData.advUrl.toString().startsWith("http") || listData.advUrl.toString()
                        .startsWith("https")
                ) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(listData.advUrl.toString().trim())
                        )
                    )
                    ad(listData)
                } else {
                    toast(getString(R.string.not_ad))
                }
            }
        })
        mRecyclerWallpaperSearch.adapter = wallpaperAdapter
        //保存热搜关键词
        saveSearch(text)
        mRecyclerWallpaperSearch.visibility = View.VISIBLE
        viewModel.getWallpaperHomeListAll(1, name = text)
        coroutineScope.launch {
            addSearchTerm(text)
        }
    }

    //保存热搜关键词
    private fun saveSearch(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val apiPostService = RetrofitUtil.getPostCall(getToken())
            val mSaveSearchCall =
                apiPostService.saveSearchKeyword(
                    AppUrl.saveSearchKeyword,
                    SaveSearchRequestData(text)
                )
            mSaveSearchCall?.enqueue(object : Callback<SuccessBean?> {
                override fun onResponse(
                    call: Call<SuccessBean?>,
                    response: Response<SuccessBean?>
                ) {
                    log(text)
                }

                override fun onFailure(call: Call<SuccessBean?>, t: Throwable) {
                    toastError()
                }
            })
        }
        hotSearchViewModel.getHotSearchList(getToken())
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

    //根据内容删除
    private suspend fun deleteSearchTerm(searchTerm: String) {
        db.searchHistoryDao().deleteSearchTerm(searchTerm)
    }

    //添加搜索历史
    private suspend fun addSearchTerm(searchTerm: String) {
        val searchHistory = SearchHistory(searchTerm = searchTerm)
        db.searchHistoryDao().insertSearchTerm(searchHistory)
    }

    //清除搜索历史
    private suspend fun clearSearchHistory() {
        db.searchHistoryDao().clearSearchHistory()
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(mEdFindWallpaper.windowToken, 0)
    }
}
