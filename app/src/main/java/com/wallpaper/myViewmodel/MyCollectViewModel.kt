package com.wallpaper.myViewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myretrofit2.RetrofitUtil
import com.myretrofit2.api.AppUrl
import com.myretrofit2.bean.MyCollectListBean
import com.myretrofit2.bean.MyFavoritesListBean
import com.myretrofit2.bean.SuccessBean
import com.myretrofit2.bean.UserDate
import com.myretrofit2.bean.UserInfo
import com.myretrofit2.bean.UserUpdateRequestData
import com.myretrofit2.bean.UserUploadImgBeam
import com.wallpaper.mylibrary.BaseApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import java.io.IOException

class MyCollectViewModel : ViewModel() {
    val mMyCollectList = MutableLiveData<MyCollectListBean>()
    fun getMyCollect(page: Int, pageSize: Int, token: String? = null) {
        viewModelScope.launch {
            mMyCollectList.value = getMyCollectList(page, pageSize, token)
        }
    }

    private suspend fun getMyCollectList(
        page: Int,
        pageSize: Int,
        token: String? = null
    ): MyCollectListBean? {
        var mMyCollectListBean: MyCollectListBean?
        return withContext(Dispatchers.IO) {
            val apiGetService = RetrofitUtil.getGetCall(token)
            val favoritesListAllCall =
                apiGetService.getMyCollectList(AppUrl.collectList, page, pageSize)
            mMyCollectListBean = try {
                val body = favoritesListAllCall?.execute()
                if (body != null && body.isSuccessful) {
                    body.body()
                }else{
                    null
                }
            } catch (e: IOException) {
                null
            }
            mMyCollectListBean
        }
    }
}