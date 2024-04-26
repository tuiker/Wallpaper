package com.wallpaper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.wallpaper.databinding.ActivityPrivacyPolicyBinding
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