package com.wallpaper.mylibrary

import android.app.Application
import android.content.Context

class BaseApplication : Application() {

    companion object {
        lateinit var application: BaseApplication
        fun getApplicationContext(): Context {
            return application
        }
    }

    override fun onCreate() {
        super.onCreate()
        //8579df9f5096457bb427dcfce2ff1c17 0998b4c2b026487886ad6e71e0a8436d
//        SharedPreferencesManager.getInstance(this).saveString("token","20faa2ddde754f92a55fd57508729aee")
        application=this
    }
}