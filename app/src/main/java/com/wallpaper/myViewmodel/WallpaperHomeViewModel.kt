package com.wallpaper.myViewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myretrofit2.RetrofitUtil
import com.myretrofit2.api.AppUrl
import com.myretrofit2.bean.WallpaperCategoryListAllBean
import com.myretrofit2.bean.WallpaperHomeBean
import com.myretrofit2.bean.WallpaperPageAllBean
import com.wallpaper.mylibrary.BaseApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException

class WallpaperHomeViewModel : ViewModel() {
    val mHomeListAll = MutableLiveData<WallpaperHomeBean>()
    fun getWallpaperHomeListAll(page: Int, pageSize: Int?=10, name: String? = null) {
        viewModelScope.launch {
            mHomeListAll.value = getHomeListAll(page, pageSize, name)
        }
    }

    private suspend fun getHomeListAll(
        page: Int,
        pageSize: Int?=10,
        name: String? = null
    ): WallpaperHomeBean? {
        var mWallpaperHomeBean: WallpaperHomeBean? = null
        return withContext(Dispatchers.IO) {
            val apiGetService = RetrofitUtil.getGetCall()
            val wallpaperHomeListCall =
                apiGetService.wallpaperHomeList(AppUrl.wallpaperHomeListAll, page, pageSize, name)
            try {
                val body = wallpaperHomeListCall?.execute()
                Log.e("---TAG---", "getHomeListAll: ${body?.code()}")
                if (body != null && body.isSuccessful) {
                    mWallpaperHomeBean = body.body()
                }
            } catch (e: IOException) {
                Log.e("---TAG---", "getHomeListAll: ${e.message}")
                mWallpaperHomeBean = null
            }
            mWallpaperHomeBean
        }
    }
}