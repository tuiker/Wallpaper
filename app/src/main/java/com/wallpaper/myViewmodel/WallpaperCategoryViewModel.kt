package com.wallpaper.myViewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myretrofit2.RetrofitUtil
import com.myretrofit2.api.AppUrl
import com.myretrofit2.bean.WallpaperCategoryListAllBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class WallpaperCategoryViewModel : ViewModel() {
    val mCategoryListAll = MutableLiveData<WallpaperCategoryListAllBean>()
    fun getCategoryListAll() {
        viewModelScope.launch {
            mCategoryListAll.value = getDataWallpaperCategoryListAllBean()
        }
    }

    private suspend fun getDataWallpaperCategoryListAllBean(): WallpaperCategoryListAllBean? {
        var mWallpaperCategoryListAllBean: WallpaperCategoryListAllBean?
        return withContext(Dispatchers.IO) {
            val apiGetService = RetrofitUtil.getGetCall()
            val categoryListAllCall =
                apiGetService.wallpaperCategoryListAll(AppUrl.wallpaperListAll)
            mWallpaperCategoryListAllBean = try {
                val body = categoryListAllCall?.execute()
                if (body != null && body.isSuccessful) {
                    body.body()
                } else {
                    null
                }
            } catch (e: IOException) {
                null
            }
            mWallpaperCategoryListAllBean
        }
    }
}