package com.myretrofit2.bean

data class WallpaperAdBean(
    val code: Int,
    val `data`: AdData,
    val msg: String
)

data class AdData(
    val advContents: String,
    val advDesc: String,
    val advName: String,
    val advUrl: String,
    val contentsType: Int,
    val id: Int,
    val urlType: Int
)