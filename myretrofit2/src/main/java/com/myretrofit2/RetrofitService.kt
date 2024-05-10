package com.myretrofit2

import com.myretrofit2.bean.AdRecordRequestData
import com.myretrofit2.bean.AddCollectRequestData
import com.myretrofit2.bean.FavoritesData
import com.myretrofit2.bean.HistoryListBean
import com.myretrofit2.bean.HotSearchListBean
import com.myretrofit2.bean.MyCollectListBean
import com.myretrofit2.bean.MyFavoritesListBean
import com.myretrofit2.bean.SaveSearchRequestData
import com.myretrofit2.bean.SendSmsBean
import com.myretrofit2.bean.SetWallpaperRecordRequestData
import com.myretrofit2.bean.SuccessBean
import com.myretrofit2.bean.UserFeedBackRequestData
import com.myretrofit2.bean.UserInfo
import com.myretrofit2.bean.UserLoginBean
import com.myretrofit2.bean.UserLoginRequestData
import com.myretrofit2.bean.UserUpdateRequestData
import com.myretrofit2.bean.UserUploadImgBeam
import com.myretrofit2.bean.WallpaperAdBean
import com.myretrofit2.bean.WallpaperCategoryListAllBean
import com.myretrofit2.bean.WallpaperDetailsIdBean
import com.myretrofit2.bean.WallpaperHomeBean
import com.myretrofit2.bean.WallpaperPageAllBean
import okhttp3.FormBody
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Url

class RetrofitService {
    interface ApiGetService {
        //壁纸分类
        @GET
        fun wallpaperCategoryListAll(
            @Url path: String,
        ): Call<WallpaperCategoryListAllBean?>?

        //发送验证码
        @GET
        fun sendRegisterOrLoginSms(
            @Url path: String,
            @Query("phone") phone: String
        ): Call<SendSmsBean?>?

        //首页壁纸
        @GET
        fun wallpaperHomeList(
            @Url path: String,
            @Query("page") page: Int,
            @Query("pageSize") pageSize: Int?,
            @Query("name") name: String?
        ): Call<WallpaperHomeBean?>?

        //分页查询壁纸
        @GET
        fun wallpaperPageList(
            @Url path: String,
            @Query("page") page: Int,
            @Query("pageSize") pageSize: Int,
            @Query("categoryId") categoryId: Int,
            @Query("name") name: String
        ): Call<WallpaperPageAllBean?>?

        //分页查询收藏里的壁纸
        @GET
        fun favoritesWallpaper(
            @Url path: String,
            @Query("page") page: Int,
            @Query("pageSize") pageSize: Int,
            @Query("favoritesId") favoritesId: Int,
        ): Call<WallpaperHomeBean?>?

        //根据id查询壁纸
        @GET
        fun getWallpaperDetailsID(
            @Url path: String,
            @Query("id") page: Int,
        ): Call<WallpaperDetailsIdBean?>?

        //获取用户信息
        @GET
        fun getUserInfo(
            @Url path: String,
        ): Call<UserInfo?>?

        //壁纸收藏夹
        @GET
        fun getMyFavoritesList(
            @Url path: String,
        ): Call<MyFavoritesListBean?>?

        //查询壁纸收藏list
        @GET
        fun getMyCollectList(
            @Url path: String,
            @Query("page") page: Int,
            @Query("pageSize") pageSize: Int
        ): Call<MyCollectListBean?>?

        //查询广告
        @GET
        fun getWallpaperAd(
            @Url path: String,
        ): Call<WallpaperAdBean?>?
        //获取热搜关键词列表
        @GET
        fun findTopHotSearchList(
            @Url path: String,
        ): Call<HotSearchListBean?>?
    }

    interface ApiPostService {
        @POST
        fun userLogin(
            @Url path: String,
            @Body requestBody: UserLoginRequestData?,
        ): Call<UserLoginBean?>?

        //修改用户信息
        @Headers("Content-Type: application/json")
        @POST
        fun userUpdate(
            @Url path: String,
            @Body requestBody: UserUpdateRequestData?,
        ): Call<SuccessBean?>?

        //图片上传
        @Multipart
        @POST
        fun updateFile(
            @Url path: String,
            @Query("gameName") gameName: String,
            @Part files: List<MultipartBody.Part>,
        ): Call<UserUploadImgBeam?>?

        //用户反馈
        @Headers("Content-Type: application/json")
        @POST
        fun userFeedback(
            @Url path: String,
            @Body requestBody: UserFeedBackRequestData?,
        ): Call<SuccessBean?>?

        //用户反馈
        @Headers("Content-Type: application/json")
        @POST
        fun unFavoriteWallpaper(
            @Url path: String,
            @Body wallpaperId: AddCollectRequestData?,
        ): Call<SuccessBean?>?

        //收藏壁纸
        @Headers("Content-Type: application/json")
        @POST
        fun addCollectWallpaper(
            @Url path: String,
            @Body wallpaperId: AddCollectRequestData?,
        ): Call<SuccessBean?>?

        //设置壁纸埋点
        @Headers("Content-Type: application/json")
        @POST
        fun setWallpaperRecord(
            @Url path: String,
            @Body wallpaperId: SetWallpaperRecordRequestData?,
        ): Call<SuccessBean?>?

        //广告埋点
        @Headers("Content-Type: application/json")
        @POST
        fun adRecord(
            @Url path: String,
            @Body wallpaperId: AdRecordRequestData?,
        ): Call<SuccessBean?>?

        //壁纸历史
        @POST
        fun historyList(
            @Url path: String,
            @Query("page") page: Int,
            @Query("pageSize") pageSize: Int
        ): Call<HistoryListBean?>?
        //保存热搜关键词
        @POST
        fun saveSearchKeyword(
            @Url path: String,
            @Body requestBody: SaveSearchRequestData?,
        ): Call<SuccessBean?>?
    }
}