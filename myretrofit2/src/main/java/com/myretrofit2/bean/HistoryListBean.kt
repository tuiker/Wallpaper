package com.myretrofit2.bean

data class HistoryListBean(
    val code: Int,
    val `data`: HistoryData,
    val msg: String
)

data class HistoryData(
    val list: List<HistoryDetails>,
    val total: Int
)

data class HistoryDetails(
    val details: String,
    val id: Int,
    val wallpaperDownloadUrl: String,
    val isCollection: Int,
    val name: String
)