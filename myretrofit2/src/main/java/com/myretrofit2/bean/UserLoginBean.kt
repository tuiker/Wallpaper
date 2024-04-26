package com.myretrofit2.bean

data class UserLoginBean(
    val code: Int,
    val `data`: UserLoginData,
    val msg: String
)

data class UserLoginData(
    val headImg: String,
    val id: Int,
    val nickname: String,
    val phone: String,
    val token: String
)