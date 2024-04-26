package com.myretrofit2.bean

data class UserInfo(
    val code: Int,
    val `data`: UserData,
    val msg: String
)

data class UserData(
    val birthday: String,
    val gender: Int,
    val headImg: String,
    val id: Int,
    val nickname: String,
    val phone: String,
    val signature: String
)