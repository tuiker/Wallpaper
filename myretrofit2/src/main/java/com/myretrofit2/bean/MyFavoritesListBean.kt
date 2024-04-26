package com.myretrofit2.bean

data class MyFavoritesListBean(
    val code: Int,
    val `data`: List<FavoritesData>,
    val msg: String
)

data class FavoritesData(
    val createTime: String,
    val describe: String,
    val id: Int,
    val title: String,
    val userId: Int
)