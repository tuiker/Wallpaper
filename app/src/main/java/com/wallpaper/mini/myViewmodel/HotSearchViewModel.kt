package com.wallpaper.mini.myViewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myretrofit2.RetrofitUtil
import com.myretrofit2.api.AppUrl
import com.myretrofit2.bean.HistoryListBean
import com.myretrofit2.bean.HotSearchListBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class HotSearchViewModel : ViewModel() {
    val mHotSearch = MutableLiveData<HotSearchListBean>()
    fun getHotSearchList(token: String? = null) {
        viewModelScope.launch {
            mHotSearch.value = getHotSearchBean(token)
        }
    }

    private suspend fun getHotSearchBean(
        token: String? = null
    ): HotSearchListBean? {
        var mHotSearchBean: HotSearchListBean?
        return withContext(Dispatchers.IO) {
            val apiPostService = RetrofitUtil.getGetCall(token)
            val mHotSearchCall =
                apiPostService.findTopHotSearchList(AppUrl.findTopHotSearchList)
            mHotSearchBean = try {
                val body = mHotSearchCall?.execute()
                Log.e("---TAG---", "getHotSearchBean: ${body?.code()}" )
                Log.e("---TAG---", "getHotSearchBean: ${body?.body()}" )
                if (body != null && body.isSuccessful) {
                    body.body()
                } else {
                    null
                }
            } catch (e: IOException) {
                null
            }
            mHotSearchBean
        }
    }
}