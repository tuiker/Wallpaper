package com.wallpaper.myViewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myretrofit2.RetrofitUtil
import com.myretrofit2.api.AppUrl
import com.myretrofit2.bean.HistoryListBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class WallpaperHistoryViewModel : ViewModel() {
    val mHistoryListAll = MutableLiveData<HistoryListBean>()
    fun getHistoryList(page: Int, pageSize: Int, token: String? = null) {
        viewModelScope.launch {
            mHistoryListAll.value = getWallpaperHistoryBean(page, pageSize, token)
        }
    }

    private suspend fun getWallpaperHistoryBean(
        page: Int,
        pageSize: Int,
        token: String? = null
    ): HistoryListBean? {
        var mHistoryListBean: HistoryListBean?
        return withContext(Dispatchers.IO) {
            val apiPostService = RetrofitUtil.getPostCall(token)
            val mHistoryListAllCall =
                apiPostService.historyList(AppUrl.historyList, page, pageSize)
            mHistoryListBean = try {
                val body = mHistoryListAllCall?.execute()
                if (body != null && body.isSuccessful) {
                    body.body()
                } else {
                    null
                }
            } catch (e: IOException) {
                null
            }
            mHistoryListBean
        }
    }
}