package com.wallpaper.mylibrary.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StringToList {
    companion object {
        fun stringToList(str: String?): List<String> {
            str.let {
                val imgUrlListType = object : TypeToken<List<String>>() {}.type
                return Gson().fromJson(str, imgUrlListType)
            }
        }
    }
}