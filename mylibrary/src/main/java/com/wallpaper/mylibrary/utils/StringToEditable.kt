package com.wallpaper.mylibrary.utils

import android.text.Editable
import android.util.Log
import kotlin.math.log

class StringToEditable {
    companion object{
        fun stringToEditable(text: String?): Editable {
            return text?.let {
                // 如果 text 不为 null，则转换为 Editable 对象
                Editable.Factory.getInstance().newEditable(text)
            } ?: Editable.Factory.getInstance().newEditable("")
        }
    }
}