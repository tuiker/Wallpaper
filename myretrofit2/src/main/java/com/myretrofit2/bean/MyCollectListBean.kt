package com.myretrofit2.bean

data class MyCollectListBean(
    val code: Int,
    val `data`: CollectData,
    val msg: String
)

data class CollectData(
    val list: List<CollectDetails>,
    val total: Int
)

data class CollectDetails(
    val createTime: String,
    val describe: String,
    val id: Int,
    val imgUrlList: String,
    val title: String,
    val coverImage: String
)