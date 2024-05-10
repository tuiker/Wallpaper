package com.wallpaper.mini.myViewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myretrofit2.RetrofitUtil
import com.myretrofit2.api.AppUrl
import com.myretrofit2.bean.WallpaperHomeBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class FavoritesListViewModel : ViewModel() {
    val mFavoritesList = MutableLiveData<WallpaperHomeBean>()
    fun getFavoritesList(page: Int, pageSize: Int, favoritesId: Int, token: String? = null) {
        viewModelScope.launch {
            mFavoritesList.value = getFavoritesListData(page, pageSize, favoritesId, token)
        }
    }

    private suspend fun getFavoritesListData(
        page: Int,
        pageSize: Int,
        favoritesId: Int,
        token: String? = null
    ): WallpaperHomeBean? {
        var mMyFavoritesListBean: WallpaperHomeBean?
        return withContext(Dispatchers.IO) {
            val apiGetService = RetrofitUtil.getGetCall(token)
            val favoritesListAllCall =
                apiGetService.favoritesWallpaper(
                    AppUrl.favoritesWallpaper,
                    page,
                    pageSize,
                    favoritesId
                )
//            favoritesListAllCall?.enqueue(object:Callback<WallpaperHomeBean?>{
//                override fun onResponse(
//                    call: Call<WallpaperHomeBean?>,
//                    response: Response<WallpaperHomeBean?>
//                ) {
//                    Log.e("---TAG---", "onResponse: ${response.body()}" )
//                }
//
//                override fun onFailure(call: Call<WallpaperHomeBean?>, t: Throwable) {
//                    Log.e("---TAG---", "onFailure: ${t.message}" )
//                }
//            })
            mMyFavoritesListBean = try {
                val body = favoritesListAllCall?.execute()
                if (body != null && body.isSuccessful) {
                    body.body()
                } else {
                    null
                }
            } catch (e: IOException) {
                null
            }
            mMyFavoritesListBean
        }
    }
}