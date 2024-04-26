package com.wallpaper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.wallpaper.mylibrary.BaseApplication

class WallpaperChangeReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("---TAG---", "-----------------" )
        if (intent?.action==Intent.ACTION_WALLPAPER_CHANGED) {
            settingWallpaperToast(context)
        }
    }
    private fun settingWallpaperToast(context: Context?) {
        val inflater = LayoutInflater.from(context)
        val layout: View = inflater.inflate(R.layout.dialog_toast, null)
        val toast = Toast(BaseApplication.application)
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()
    }
}