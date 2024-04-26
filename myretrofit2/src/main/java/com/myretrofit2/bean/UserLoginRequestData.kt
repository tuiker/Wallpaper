package com.myretrofit2.bean

data class UserLoginRequestData(
    val phone: String,
    val verifyCode: String
)