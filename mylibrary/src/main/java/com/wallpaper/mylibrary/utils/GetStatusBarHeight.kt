package com.wallpaper.mylibrary.utils

import android.content.Context
import android.content.res.Resources
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout

class GetStatusBarHeight {
    companion object {
        //设置状态栏顶部高度
        fun setBarHeight(view: View,context: Context,left:Int, top:Int, right:Int, bottom:Int){
            val params = view.layoutParams as ConstraintLayout.LayoutParams
            params.setMargins(left, getStatusBarHeight(context) +top, right, bottom)
        }
        fun getStatusBarHeight(context: Context): Int {
            val resources: Resources = context.resources
            val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
            return if (resourceId > 0) {
                resources.getDimensionPixelSize(resourceId)
            } else {
                // 默认情况下，状态栏高度可以设置为一个合适的值，例如48dp
                // 如果无法从资源中获取，可以返回一个默认值
                // 注意：这个默认值可能不适用于所有设备，请根据需要进行调整
                (48 * resources.displayMetrics.density).toInt()
            }
        }
    }
}