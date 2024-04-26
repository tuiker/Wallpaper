package com.myretrofit2.bean

data class WallpaperCategoryListAllBean(
    val code: Int,
    val `data`: List<Data>,
    val msg: String
)

data class Data(
    val createTime: String,
    val details: String,
    val downloadNum: Int,
    val id: Int,
    val name: String,
    val wallpaperNum: Int
)