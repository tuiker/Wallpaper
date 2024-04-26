package com.myretrofit2.bean

data class UserUpdateRequestData(
    var id: Int? = null,
    var birthday: String? = null,
    var gender: Int? = null,
    var headImg: String? = null,
    var nickname: String? = null,
    var signature: String? = null
)