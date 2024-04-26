package com.myretrofit2.bean

data class HotSearchListBean(
    val code: Int,
    val `data`: List<HotSearchData>,
    val msg: String
)

data class HotSearchData(
    val id: Int,
    val keyword: String,
    val searchNum: Int
)