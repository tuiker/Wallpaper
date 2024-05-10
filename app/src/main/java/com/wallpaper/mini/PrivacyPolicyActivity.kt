package com.wallpaper.mini

import android.view.View
import com.wallpaper.mini.databinding.ActivityPrivacyPolicyBinding
import com.wallpaper.mylibrary.BaseActivity

class PrivacyPolicyActivity : BaseActivity<ActivityPrivacyPolicyBinding>() {
    override fun getView(): ActivityPrivacyPolicyBinding {
        return ActivityPrivacyPolicyBinding.inflate(layoutInflater)
    }

    override fun init() {
    }

    fun ivBack(view: View) {
        finish()
    }
}