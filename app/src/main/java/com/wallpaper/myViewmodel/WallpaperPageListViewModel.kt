package com.wallpaper.myViewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myretrofit2.RetrofitUtil
import com.myretrofit2.api.AppUrl
import com.myretrofit2.bean.WallpaperCategoryListAllBean
import com.myretrofit2.bean.WallpaperPageAllBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class WallpaperPageListViewModel : ViewModel() {
    val mCategoryListAll = MutableLiveData<WallpaperPageAllBean>()
    fun getCategoryListAll(page: Int, categoryId: Int) {
        viewModelScope.launch {
            mCategoryListAll.value = getWallpaperPageListAllBean(page, categoryId)
        }
    }

    private suspend fun getWallpaperPageListAllBean(
        page: Int,
        categoryId: Int
    ): WallpaperPageAllBean? {
        var mWallpaperPageAllBean: WallpaperPageAllBean?
        return withContext(Dispatchers.IO) {
            val apiGetService = RetrofitUtil.getGetCall()
            val categoryListAllCall =
                apiGetService.wallpaperPageList(AppUrl.wallpaperPageList, page, 10, categoryId, "")
            mWallpaperPageAllBean = try {
                val body = categoryListAllCall?.execute()
                if (body != null && body.isSuccessful) {
                    body.body()
                } else {
                    null
                }
            } catch (e: IOException) {
                Log.e("---TAG---", "getHomeListAll: ${e.message}")
                null
            }
            mWallpaperPageAllBean
        }
    }
}