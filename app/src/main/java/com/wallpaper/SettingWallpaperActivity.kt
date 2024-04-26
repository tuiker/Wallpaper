package com.wallpaper

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.WallpaperManager
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.CountDownTimer
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.myretrofit2.RetrofitUtil
import com.myretrofit2.api.AppUrl
import com.myretrofit2.bean.AdRecordRequestData
import com.myretrofit2.bean.AddCollectRequestData
import com.myretrofit2.bean.FavoritesData
import com.myretrofit2.bean.SetWallpaperRecordRequestData
import com.myretrofit2.bean.SuccessBean
import com.wallpaper.adapter.WallpaperCategoryAdapter
import com.wallpaper.databinding.ActivitySettingWallpaperBinding
import com.wallpaper.myView.ImageAvatar
import com.wallpaper.myViewmodel.MyFavoritesViewModel
import com.wallpaper.myViewmodel.WallpaperAdViewModel
import com.wallpaper.myViewmodel.WallpaperDetailsViewModel
import com.wallpaper.mylibrary.BaseActivity
import com.wallpaper.mylibrary.utils.GetStatusBarHeight
import com.wallpaper.mylibrary.utils.MyGlide
import com.wallpaper.mylibrary.utils.StringToList
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.listener.OnPageChangeListener
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class SettingWallpaperActivity : BaseActivity<ActivitySettingWallpaperBinding>() {
    private lateinit var mIvBack: ImageView
    private lateinit var mIvAvatar: ImageAvatar
    private lateinit var mRtSettingWallpaper: Button
    private lateinit var mRtLike: RelativeLayout
    private lateinit var wallpaperChangeReceiver: WallpaperChangeReceiver
    private lateinit var mConstraintLayoutAuthor: ConstraintLayout
    private lateinit var mWallpaperDetailsViewModel: WallpaperDetailsViewModel
    private lateinit var mIvIsCollection: ImageView
    private lateinit var mUsername: TextView
    private lateinit var mTvAuthorTitle: TextView
    private lateinit var mTvAuthorIntroduce: TextView
    private lateinit var mTvLikeNumber: TextView
    private lateinit var mTvDownloadNumber: TextView
    private var isAnimate = true
    private var wallpaperId by Delegates.notNull<Int>()
    private val mAddCollectID: AddCollectRequestData = AddCollectRequestData()
    private lateinit var mMyFavoritesViewModel: MyFavoritesViewModel
    private lateinit var mWallpaperAdViewModel: WallpaperAdViewModel
    private var isSetWallpaper: Boolean = false
    private lateinit var wallpaperImage: String
    private lateinit var banner: Banner<String, BannerImageAdapter<String>>
    private var playerView: PlayerView? = null
    private var countdownTimer: CountDownTimer? = null
    private lateinit var lottie: LottieAnimationView
    override fun getView(): ActivitySettingWallpaperBinding {
        return ActivitySettingWallpaperBinding.inflate(layoutInflater)
    }

    override fun init() {
        wallpaperChangeReceiver = WallpaperChangeReceiver()
        val intentFilter = IntentFilter(Intent.ACTION_WALLPAPER_CHANGED)
        registerReceiver(wallpaperChangeReceiver, intentFilter)
        initView()
        initData()
        initEvent()
    }

    private fun initData() {
        banner = vb.banner as Banner<String, BannerImageAdapter<String>>
        //获取广告
        mWallpaperAdViewModel = ViewModelProvider(this)[WallpaperAdViewModel::class.java]
        mWallpaperAdViewModel.getAd(getToken())
        //壁纸详情
        wallpaperId = intent.getIntExtra("wallpaperId", 0)
        mAddCollectID.wallpaperId = wallpaperId
        mWallpaperDetailsViewModel = ViewModelProvider(this)[WallpaperDetailsViewModel::class.java]
        mWallpaperDetailsViewModel.getDetailsAll(wallpaperId, getToken())
        mWallpaperDetailsViewModel.mWallpaperDetailsAll.observe(this) {
            log("$it")
            if (it != null) {
                when (it.code) {
                    1000 -> {
                        val detailsData = it.data
                        log("壁纸详情-->$detailsData")
                        mAddCollectID.wallpaperId = detailsData.id
                        log("头像-->${detailsData.headImg}")
                        Glide.with(application)
                            .load(detailsData.headImg)
                            .transform(
                                MultiTransformation(
                                    CenterCrop(),
                                    RoundedCornersTransformation(
                                        200, // 设置圆角半径
                                        0, // 不设置任何圆角的角
                                        RoundedCornersTransformation.CornerType.ALL // 选择要设置圆角的角
                                    ),
                                )
                            )
                            .into(mIvAvatar)
                        if (detailsData.imgUrlList != null) {
                            val imgUrlList = StringToList.stringToList(detailsData.imgUrlList)
                            wallpaperImage = imgUrlList[0]
                            banner.setAdapter(object : BannerImageAdapter<String>(imgUrlList) {
                                override fun onBindView(
                                    holder: BannerImageHolder?,
                                    data: String?,
                                    position: Int,
                                    size: Int
                                ) {
                                    if (holder != null) {
                                        MyGlide.setGlideImage(application, data, holder.imageView)
                                        holder.itemView.setOnClickListener {
                                            mConstraintLayoutAuthor.animate()
                                                .translationY(if (isAnimate) mConstraintLayoutAuthor.height.toFloat() else 0f)
                                                .setDuration(600)
                                                .start()
                                            isAnimate = !isAnimate
                                        }
                                    }
                                }
                            }).addOnPageChangeListener(object : OnPageChangeListener {
                                override fun onPageScrolled(
                                    position: Int,
                                    positionOffset: Float,
                                    positionOffsetPixels: Int
                                ) {
                                }

                                override fun onPageSelected(position: Int) {
                                    log("滑动壁纸-->${imgUrlList[position]}")
                                    wallpaperImage = imgUrlList[position]
                                }

                                override fun onPageScrollStateChanged(state: Int) {
                                }
                            })
                        }

                        mUsername.text = detailsData.nickname
                        mTvAuthorTitle.text = detailsData.name
                        mTvAuthorIntroduce.text = detailsData.details
                        mTvDownloadNumber.text = detailsData.downloadNum.toString()
                        mTvLikeNumber.text = detailsData.collectNum.toString()
                        if (detailsData.isCollect) {
                            mIvIsCollection.setImageResource(R.drawable.is_like)
                            mRtLike.tag = true
                        } else {
                            mRtLike.tag = false
                        }
                        log("${mRtLike.tag}")
                    }

                    401 -> toPageFinish(LoginActivity::class.java)
                }
            } else {
                toastError()
            }
        }
        //壁纸分类
        mMyFavoritesViewModel = ViewModelProvider(this)[MyFavoritesViewModel::class.java]
        mMyFavoritesViewModel.getMyFavoritesList(getToken())
    }

    private fun initView() {
        mIvBack = vb.back
        mRtSettingWallpaper = vb.settingWallpaper
        mRtLike = vb.rtLike
        mIvIsCollection = vb.ivLike
        mIvAvatar = vb.ivAvatar
        mUsername = vb.tvUserName
        mTvAuthorTitle = vb.tvAuthorTitle
        mTvAuthorIntroduce = vb.tvAuthorIntroduce
        mTvLikeNumber = vb.tvLikeNumber
        mTvDownloadNumber = vb.tvDownloadNumber
        mConstraintLayoutAuthor = vb.constraintLayoutAuthor
        lottie = vb.lottieCollect
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initEvent() {
        GetStatusBarHeight.setBarHeight(mIvBack, application, 24, 24, 0, 0)

        mIvBack.setOnClickListener {
            finish()
        }
        //设置壁纸
        mRtSettingWallpaper.setOnClickListener {
//            if (isSetWallpaper) {
//                vb.lottie.visibility = View.VISIBLE
//                mRtSettingWallpaper.apply {
//                    isEnabled = false
//                    text = ""
//                }
//                Glide.with(application)
//                    .asBitmap()
//                    .load(wallpaperImage)
//                    .into(object : CustomTarget<Bitmap>() {
//                        override fun onResourceReady(
//                            resource: Bitmap,
//                            transition: Transition<in Bitmap>?
//                        ) {
//                            userSetWallpaper(resource, wallpaperImage)
//                        }
//
//                        override fun onLoadCleared(placeholder: Drawable?) {
//                        }
//                    })
//            } else {
            dialogAd()
//            }
//            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.img1)
//            val wallpaperManager = WallpaperManager.getInstance(applicationContext)
            //左面和锁屏
//            wallpaperManager.setBitmap(bitmap)
            //锁屏
//            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
            //桌面
//            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
        }
        //收藏
        mRtLike.setOnClickListener {
            log("${mRtLike.tag}")
            if (mRtLike.tag == true) {
                mRtLike.isEnabled = false
                unFavorite()
                return@setOnClickListener
            }
            likeDialog()
        }
    }

    private fun unFavorite() {
        lottie.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val apiPostService = RetrofitUtil.getPostCall(getToken())
            val addCollectCall =
                apiPostService.addCollectWallpaper(AppUrl.unFavorite, mAddCollectID)
            val response = addCollectCall?.execute()
            withContext(Dispatchers.Main) {
                if (response != null && response.isSuccessful) {
                    if (response.body()?.data == true) {
                        log("取消收藏-->${response.body()}")
                        mIvIsCollection.setImageResource(R.drawable.btn_like)
                        mTvLikeNumber.text =
                            (mTvLikeNumber.text.toString().toInt() - 1).toString()
                        mRtLike.tag = false
                    }
                } else {
                    toastError()
                }
                lottie.visibility = View.GONE
                mRtLike.isEnabled = true
            }
        }
    }

    //设置壁纸和埋点
    fun userSetWallpaper(bitmap: Bitmap?, wallpaperImage: String) {
        if (bitmap != null) {
            val wallpaperManager = WallpaperManager.getInstance(application)
            try {
                wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                val apiPostService = RetrofitUtil.getPostCall(getToken())
                val apiPostCall =
                    apiPostService.setWallpaperRecord(
                        AppUrl.setWallpaperRecord,
                        SetWallpaperRecordRequestData(mAddCollectID.wallpaperId, wallpaperImage)
                    )
                apiPostCall?.enqueue(object : Callback<SuccessBean?> {
                    override fun onResponse(
                        call: Call<SuccessBean?>,
                        response: Response<SuccessBean?>
                    ) {
                        if (response.isSuccessful) {
                            log("设置壁纸")
                        }
                    }

                    override fun onFailure(call: Call<SuccessBean?>, t: Throwable) {
                    }
                })
                isSetWallpaper = false
                vb.lottie.visibility = View.GONE
                mRtSettingWallpaper.apply {
                    isEnabled = true
                    text = getString(R.string.setting_wallpaper)
                }
            } catch (e: Exception) {
                log(e.toString())
            }
        }
    }

    //收藏弹窗
    @SuppressLint("SetTextI18n")
    private fun likeDialog() {
        val likeDialog = Dialog(this)
        likeDialog.setContentView(R.layout.wallpaper_like_dialog)
        likeDialog.setCanceledOnTouchOutside(false)
        likeDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val mRecyclerView = likeDialog.findViewById<RecyclerView>(R.id.wallpaper_category)
        mRecyclerView.layoutManager = LinearLayoutManager(application)
        val wallpaperCategoryAdapter =
            WallpaperCategoryAdapter(object : WallpaperCategoryAdapter.ItemClickListener {
                override fun onItemClicked(favoritesData: FavoritesData) {
                    mAddCollectID.favoritesId = favoritesData.id
                }
            })
        val dialogImg = likeDialog.findViewById<ImageView>(R.id.iv_wallpaper_dialog)
        val dialogTitle = likeDialog.findViewById<TextView>(R.id.tv_dialog_title)
        val dialogName = likeDialog.findViewById<TextView>(R.id.tv_dialog_name)
        mWallpaperDetailsViewModel.mWallpaperDetailsAll.observe(this) {
            log("壁纸详情-->$it")
            if (it != null) {
                when (it.code) {
                    1000 -> {
                        val detailsData = it.data
                        val imgUrlList = StringToList.stringToList(detailsData.imgUrlList)
                        MyGlide.setGlideImage(application, imgUrlList[0], dialogImg, radius = 10)
                        dialogName.text = detailsData.name
                        dialogTitle.text = detailsData.categoryName
                        if (detailsData.isCollect) {
                            mIvIsCollection.setImageResource(R.drawable.is_like)
                            mRtLike.tag = true
                        }
                    }

                    401 -> toPageFinish(LoginActivity::class.java)
                }

            } else {
                toastError()
            }
        }
        //壁纸收藏夹
        mMyFavoritesViewModel.mMyFavoritesList.observe(this) {
            log("壁纸收藏夹-->${it}")
            if (it != null && it.code == 1000) {
                wallpaperCategoryAdapter.submitList(it.data)
            } else {
                toastError()
            }
        }
        mRecyclerView.adapter = wallpaperCategoryAdapter
        likeDialog.findViewById<View>(R.id.iv_close).setOnClickListener {
            likeDialog.dismiss()
        }
        //确认收藏
        val addCollectBtn = likeDialog.findViewById<Button>(R.id.btn_addcollect)
        addCollectBtn.setOnClickListener {
            if (mAddCollectID.favoritesId == null) {
                toast(getString(R.string.choose_wallpaper_type))
                return@setOnClickListener
            }
            log("$mAddCollectID")
            val lottie = likeDialog.findViewById<LottieAnimationView>(R.id.lottie_loading)
            lottie.visibility = View.VISIBLE
            addCollectBtn.isEnabled = false
            CoroutineScope(Dispatchers.IO).launch {
                val apiPostService = RetrofitUtil.getPostCall(getToken())
                val addCollectCall =
                    apiPostService.addCollectWallpaper(AppUrl.addCollect, mAddCollectID)
                val response = addCollectCall?.execute()
                withContext(Dispatchers.Main) {
                    log("${response?.code()}")
                    if (response != null && response.isSuccessful) {
                        if (response.body()?.data == true) {
                            log("${response.body()}")
                            mIvIsCollection.setImageResource(R.drawable.is_like)
                            mTvLikeNumber.text =
                                (mTvLikeNumber.text.toString().toInt() + 1).toString()
                            likeDialog.dismiss()
                            mRtLike.tag = true
                        }
                    } else {
                        toastError()
                        lottie.visibility = View.GONE
                        mRtLike.tag = false
                    }
                    addCollectBtn.isEnabled = true
                }
            }
        }
        likeDialog.show() // 显示弹窗
    }

    //广告弹窗
    @OptIn(UnstableApi::class)
    private fun dialogAd() {
        val adDialog = Dialog(this)
        adDialog.setContentView(R.layout.wallpaper_ad_dialog)
        adDialog.setCanceledOnTouchOutside(false)
        adDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val adCountdown = adDialog.findViewById<TextView>(R.id.tv_ad_countdown)
        val imgAd = adDialog.findViewById<ImageView>(R.id.iv_ad_img)
        val btnAd = adDialog.findViewById<Button>(R.id.btn_ad)
        adDialog.findViewById<View>(R.id.iv_ad_close).setOnClickListener {
            countdownTimer?.cancel()
            adDialog.dismiss()
        }
        playerView = adDialog.findViewById(R.id.playerView)

        playerView?.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                // 设置圆角矩形，这里的15是圆角的半径
                outline.setRoundRect(0, 0, view.width, view.height, 16f)
            }
        }
// 开启剪裁到轮廓区域
        playerView?.clipToOutline = true

        playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        val player = ExoPlayer.Builder(application).build()
        playerView?.player = player
        var videoDuration: Long = 10000
        mWallpaperAdViewModel.mWallpaperAd.observe(this) {
            log("$it")
            if (it != null && it.code == 1000) {
                val data = it.data
                if (data.contentsType.toString().toDouble().toInt() == 1) {
                    log("图片广告")
                    MyGlide.setGlideImage(application, data.advContents, imgAd, radius = 16)
                    imgAd.visibility = View.VISIBLE
                    adDialog.findViewById<LottieAnimationView>(R.id.lottie).visibility = View.GONE
                    countdown(adCountdown, videoDuration)
                } else {
                    playerView?.visibility = View.VISIBLE
                    val mediaItem = MediaItem.Builder()
                        .setUri(Uri.parse(data.advContents))
                        .build()
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                    player.addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            if (playbackState == Player.STATE_READY) {
                                // 视频准备完成后获取视频时长
                                val videoDuration = player.duration
                                adCountdown.text =
                                    "$videoDuration ${getString(R.string._downloadafter10s)}"
                                adDialog.findViewById<LottieAnimationView>(R.id.lottie).visibility =
                                    View.GONE
                                countdown(adCountdown, videoDuration)
                                log("$videoDuration")
                            }
                        }
                    })
                }
                //点击广告
                btnAd.setOnClickListener {
                    log(getToken())
                    log("${data.advUrl}")
                    if (data.advUrl.startsWith("http") || data.advUrl.startsWith("https")) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(data.advUrl)))
                    }else{
                        toast(getString(R.string.not_ad))
                    }
                    val apiPostService = RetrofitUtil.getPostCall(getToken())
                    val wallpaperHomeListCall =
                        apiPostService.adRecord(
                            AppUrl.clickAdRecord,
                            AdRecordRequestData(data.id, data.contentsType)
                        )
                    wallpaperHomeListCall?.enqueue(object : Callback<SuccessBean?> {
                        override fun onResponse(
                            call: Call<SuccessBean?>,
                            response: Response<SuccessBean?>
                        ) {
                            if (response.isSuccessful && response.code() == 200) {
                                log("点击广告")
                            }
                        }

                        override fun onFailure(call: Call<SuccessBean?>, t: Throwable) {
                        }
                    })
                }
            }
        }
        adDialog.show() // 显示弹窗
    }

    //10秒倒计时
    private fun countdown(adCountdown: TextView, duration: Long? = 10000) {
        countdownTimer = object : CountDownTimer(duration!!, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                adCountdown.text = "$seconds ${getString(R.string._downloadafter10s)}"
            }

            override fun onFinish() {
                isSetWallpaper = true
                adCountdown.text = getString(R.string.get_wallpaper)
                vb.lottie.visibility = View.VISIBLE
                mRtSettingWallpaper.apply {
                    isEnabled = false
                    text = ""
                }
                Glide.with(application)
                    .asBitmap()
                    .load(wallpaperImage)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            userSetWallpaper(resource, wallpaperImage)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
            }
        }
        countdownTimer?.start()
    }

    override fun onResume() {
        super.onResume()
        playerView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        playerView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(wallpaperChangeReceiver)
        playerView?.player?.release()
        playerView?.player = null
    }
}