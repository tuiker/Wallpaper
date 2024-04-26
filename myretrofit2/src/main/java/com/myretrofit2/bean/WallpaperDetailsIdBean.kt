package com.myretrofit2.bean

data class WallpaperDetailsIdBean(
    val code: Int,
    val `data`: DetailsData,
    val msg: String
)

data class DetailsData(
    val id: Int,
    val name: String,
    val imgUrlList: String,
    val isCollection: Int,
    val categoryId: Int,
    val categoryName: String,
    val collectNum:Int,
    val details: String,
    val downloadNum: Int,
    val userId: Int,
    val nickname: String,
    val nicknames: String,
    val headImg: String,
    val isCollect:Boolean,
    val favoritesId:Int
)