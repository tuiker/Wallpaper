package com.wallpaper.fragment

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.wallpaper.mylibrary.BaseFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.myretrofit2.bean.Data
import com.wallpaper.R
import com.wallpaper.databinding.FragmentFindBinding
import com.wallpaper.mylibrary.utils.GetStatusBarHeight
import com.wallpaper.myViewmodel.WallpaperCategoryViewModel

class FindFragment : BaseFragment<FragmentFindBinding>() {
    private lateinit var mTabLayout: TabLayout
    private lateinit var mViewPager2: ViewPager2
    private lateinit var viewModel: WallpaperCategoryViewModel
    override fun getViewBinding(): FragmentFindBinding {
        return FragmentFindBinding.inflate(layoutInflater)
    }

    override fun init() {
        initView()
        initData()
    }

    private fun initView() {
        mTabLayout = vb.tabLayout
        GetStatusBarHeight.setBarHeight(mTabLayout, application, 0, 16, 0, 0)
        mViewPager2 = vb.viewPager2
        mViewPager2.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
        mViewPager2.offscreenPageLimit = 3
        mViewPager2.isUserInputEnabled = false
    }

    private fun initData() {
        viewModel = ViewModelProvider(this)[WallpaperCategoryViewModel::class.java]
        viewModel.getCategoryListAll()
        // 使用自定义 TabLayout 样式
        val fragments = ArrayList<Fragment>()
        mTabLayout.tabRippleColor = null // 移除点击涟漪效果
        viewModel.mCategoryListAll.observe(requireActivity()) {
            if (it != null && it.code == 1000) {
                log("${it.data}")
                for (i in it.data) {
                    fragments.add(WallpaperTypeFragment.newInstance(i.id))
                }
                mViewPager2.adapter = MyPagerAdapter(activity, fragments)
                TabLayoutMediator(mTabLayout, mViewPager2) { tab, position ->
//            设置每个标签的标题
                    tab.text = it.data[position].name
                    vb.lottie.visibility= View.GONE
                }.attach()
            } else {
                vb.lottie.setAnimation(R.raw.animation_404)
                toastError()
            }
        }
    }

    private inner class MyPagerAdapter(
        fragmentActivity: FragmentActivity?,
        val fragments: ArrayList<Fragment>
    ) :
        FragmentStateAdapter(fragmentActivity!!) {
        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment {
            // 返回对应位置的 Fragment 实例
            return fragments[position]
        }
    }
}