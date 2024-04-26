package com.wallpaper.myViewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myretrofit2.RetrofitUtil
import com.myretrofit2.api.AppUrl
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

class UserInfoViewModel : ViewModel() {
    val mUserInfo = MutableLiveData<UserInfo>()
    val mUserUpdate = MutableLiveData<UserUploadImgBeam>()
    val mUserSex = MutableLiveData<Int>()
    val mUserBirth = MutableLiveData<UserDate>()
    val mUserSuccess = MutableLiveData<SuccessBean>()
    fun getUserInfo(token: String? = null) {
        viewModelScope.launch {
            mUserInfo.value = getUser(token)
        }
    }

    private suspend fun getUser(token: String? = null): UserInfo? {
        var mUserInfo: UserInfo?
        return withContext(Dispatchers.IO) {
            val apiGetService = RetrofitUtil.getGetCall(token)
            val categoryListAllCall =
                apiGetService.getUserInfo(AppUrl.getUserInfo)
            mUserInfo = try {
                val body = categoryListAllCall?.execute()
                if (body != null && body.isSuccessful) {
                    val body1 = body.body()
                    Log.e("---TAG---", "getUser: ${body1?.code}" )
                    body1
                } else {
                    null
                }
            } catch (e: IOException) {
                null
            }
            mUserInfo
        }
    }

    fun setUserImg(
        gameName: String,
        image: MutableList<MultipartBody.Part>,
        token: String? = null
    ) {
        viewModelScope.launch {
            mUserUpdate.value = setUserImage(gameName, image, token)
        }
    }

    private suspend fun setUserImage(
        gameName: String,
        image: List<MultipartBody.Part>, token: String? = null
    ): UserUploadImgBeam? {
        var mUserUploadImgBeam: UserUploadImgBeam?
        return withContext(Dispatchers.IO) {
            val apiGetService = RetrofitUtil.getPostCall(token)
            val categoryListAllCall =
                apiGetService.updateFile(AppUrl.imageFile, gameName, image)
            mUserUploadImgBeam = try {
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
            mUserUploadImgBeam
        }
    }

    fun upDataUser(mUserUpdateRequestData: UserUpdateRequestData, token: String? = null) {
        viewModelScope.launch {
            mUserSuccess.value = upDataUserRequest(mUserUpdateRequestData, token)
        }
    }

    private suspend fun upDataUserRequest(
        mUserUpdateRequestData: UserUpdateRequestData, token: String? = null
    ): SuccessBean? {
        var mSuccessBean: SuccessBean?
        return withContext(Dispatchers.IO) {
            val apiGetService = RetrofitUtil.getPostCall(token)
            val categoryListAllCall =
                apiGetService.userUpdate(AppUrl.userUpdate, mUserUpdateRequestData)
            mSuccessBean = try {

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
            mSuccessBean
        }
    }
}