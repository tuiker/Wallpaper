package com.myretrofit2.bean

data class WallpaperHomeBean(
    val code: Int,
    val `data`: HomeData,
    val msg: String
)

data class HomeData(
    val list: List<ListData>,
    val total: Int
)
