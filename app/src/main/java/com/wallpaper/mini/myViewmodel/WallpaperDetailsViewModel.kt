package com.wallpaper.mini.myViewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myretrofit2.RetrofitUtil
import com.myretrofit2.api.AppUrl
import com.myretrofit2.bean.WallpaperCategoryListAllBean
import com.myretrofit2.bean.WallpaperDetailsIdBean
import com.myretrofit2.bean.WallpaperPageAllBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import kotlin.math.log

class WallpaperDetailsViewModel : ViewModel() {
    val mWallpaperDetailsAll = MutableLiveData<WallpaperDetailsIdBean>()
    fun getDetailsAll(id: Int, token: String? = null) {
        viewModelScope.launch {
            mWallpaperDetailsAll.value = getWallpaperDetailsAllBean(id, token)
        }
    }

    private suspend fun getWallpaperDetailsAllBean(
        id: Int,
        token: String? = null
    ): WallpaperDetailsIdBean? {
        var mWallpaperDetailsIdBean: WallpaperDetailsIdBean?
        return withContext(Dispatchers.IO) {
            val apiGetService = RetrofitUtil.getGetCall(token)
            val getWallpaperDetailsIDCall =
                apiGetService.getWallpaperDetailsID(AppUrl.getWallpaperDetailsID, id)
            mWallpaperDetailsIdBean = try {
                val body = getWallpaperDetailsIDCall?.execute()
                if (body != null && body.isSuccessful) {
                    body.body()
                } else {
                    null
                }
            } catch (e: IOException) {
                null
            }
            mWallpaperDetailsIdBean
        }
    }
}