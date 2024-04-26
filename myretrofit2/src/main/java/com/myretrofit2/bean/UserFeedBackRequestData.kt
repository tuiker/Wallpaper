package com.myretrofit2.bean

import com.google.gson.annotations.SerializedName

data class UserFeedBackRequestData(
    @SerializedName("feedbackType")
    val feedbackType: Int,
    @SerializedName("feedbackContent")
    val feedbackContent: String,
    @SerializedName("feedbackImgList")
    val feedbackImgList: String? = null
)