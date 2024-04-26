package com.wallpaper

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import com.wallpaper.databinding.ActivityHomeBinding
import com.wallpaper.fragment.FindFragment
import com.wallpaper.fragment.UserFragment
import com.wallpaper.fragment.WallpaperListFragment
import com.wallpaper.mylibrary.BaseActivity

class HomeActivity : BaseActivity<ActivityHomeBinding>() {
    private lateinit var mBottomNavigation: BottomNavigationView
    private var activeFragment: Fragment? = null
    private val fragmentList = listOf(
        WallpaperListFragment(),
        FindFragment(),
        UserFragment()
    )

    override fun getView(): ActivityHomeBinding {
        return ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun init() {
        initView()
    }

    private fun initView() {
        mBottomNavigation = vb.bottomnavigation
        mBottomNavigation.setOnItemSelectedListener(onNavigationItemSelectedListener)
        mBottomNavigation.selectedItemId = R.id.navigation_home
        mBottomNavigation.itemIconTintList = null
        switchFragment(fragmentList[0])
    }

    private val onNavigationItemSelectedListener =
        NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // 设置选中状态的图标
                    item.setIcon(R.drawable.nav_wallpaper_home_selected)
                    // 取消其他项目的选中状态图标
                    mBottomNavigation.menu.findItem(R.id.navigation_find)
                        .setIcon(R.drawable.nav_find)
                    mBottomNavigation.menu.findItem(R.id.navigation_user)
                        .setIcon(R.drawable.nav_user)
                    switchFragment(fragmentList[0])
                }

                R.id.navigation_find -> {
                    // 设置选中状态的图标
                    item.setIcon(R.drawable.nav_find_selected)
                    // 取消其他项目的选中状态图标
                    mBottomNavigation.menu.findItem(R.id.navigation_home)
                        .setIcon(R.drawable.nav_wallpaper_home)
                    mBottomNavigation.menu.findItem(R.id.navigation_user)
                        .setIcon(R.drawable.nav_user)
                    switchFragment(fragmentList[1])
                }

                R.id.navigation_user -> {
                    // 设置选中状态的图标
                    item.setIcon(R.drawable.nav_user_selected)
                    // 取消其他项目的选中状态图标
                    mBottomNavigation.menu.findItem(R.id.navigation_home)
                        .setIcon(R.drawable.nav_wallpaper_home)
                    mBottomNavigation.menu.findItem(R.id.navigation_find)
                        .setIcon(R.drawable.nav_find)
                    switchFragment(fragmentList[2])
                }
            }
            true
        }


    @SuppressLint("CommitTransaction")
    private fun switchFragment(fragment: Fragment) {
        val tag = fragment.javaClass.simpleName
        val beginTransaction = supportFragmentManager.beginTransaction()
        // 隐藏当前活动的 Fragment
        activeFragment?.let {
            beginTransaction.hide(it)
        }
        // 如果 Fragment 已经添加，则显示它；否则添加并显示
        if (fragment.tag != null) {
            beginTransaction.show(fragment)
        } else {
            beginTransaction.add(R.id.frameLayout, fragment, tag)
        }
        beginTransaction
            .commit()
        activeFragment = fragment
    }
}