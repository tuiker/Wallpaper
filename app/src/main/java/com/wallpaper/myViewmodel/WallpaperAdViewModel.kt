package com.wallpaper.myViewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myretrofit2.RetrofitUtil
import com.myretrofit2.api.AppUrl
import com.myretrofit2.bean.WallpaperAdBean
import com.myretrofit2.bean.WallpaperCategoryListAllBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class WallpaperAdViewModel : ViewModel() {
    val mWallpaperAd = MutableLiveData<WallpaperAdBean>()
    fun getAd(token: String? = null) {
        viewModelScope.launch {
            mWallpaperAd.value = getAdData(token)
        }
    }

    private suspend fun getAdData(token: String? = null): WallpaperAdBean? {
        var mWallpaperAdBean: WallpaperAdBean?
        return withContext(Dispatchers.IO) {
            val apiGetService = RetrofitUtil.getGetCall(token)
            val adCall =
                apiGetService.getWallpaperAd(AppUrl.getWallpaperAdv)
            mWallpaperAdBean = try {
                val body = adCall?.execute()
                if (body != null && body.isSuccessful) {
                    body.body()
                } else {
                    null
                }
            } catch (e: IOException) {
                null
            }
            mWallpaperAdBean
        }
    }
}