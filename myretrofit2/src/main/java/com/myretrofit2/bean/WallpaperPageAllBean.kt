package com.myretrofit2.bean

data class WallpaperPageAllBean(
    val code: Int,
    val `data`: PageAllData,
    val msg: String
)

data class PageAllData(
    val list: List<ListData>,
    val total: Int
)

data class ListData(
    val advContents: Any,
    val advName: Any,
    val advUrl: Any,
    val contentsType: Any,
    val details:String,
    val downloadNum: Int,
    val nickname:String,
    val id: Int,
    val imgUrlList: String,
    val isAdv: Any,
    val isCollection: Int,
    val name: String,
    val urlType: Any
)