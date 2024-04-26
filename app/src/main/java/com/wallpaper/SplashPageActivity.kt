package com.wallpaper

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.wallpaper.mylibrary.SharedPreferencesManager

class SplashPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_page)
        ImmersionBar.with(this)
//            透明导航栏，不写默认黑色(设置此方法，fullScreen()方法自动为true)
            .transparentNavigationBar()
//            状态栏字体是深色，不写默认为亮色
            .statusBarDarkFont(false)
            .fullScreen(true)      //有导航栏的情况下，activity全屏显示，也就是activity最下面被导航栏覆盖，不写默认非全屏
            .init()
        initView()
    }

    private fun initView() {
        val token = SharedPreferencesManager.getInstance(this).getString("token", "")
        findViewById<View>(R.id.iv_next).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    if (token == "") LoginActivity::class.java else HomeActivity::class.java
                )
            )
            finish()
        }
    }
}