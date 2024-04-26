package com.wallpaper

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.github.gzuliyujiang.imagepicker.ImagePicker
import com.github.gzuliyujiang.imagepicker.PickCallback
import com.myretrofit2.RetrofitUtil
import com.myretrofit2.api.AppUrl
import com.myretrofit2.bean.SuccessBean
import com.myretrofit2.bean.UserFeedBackRequestData
import com.wallpaper.databinding.ActivityUserFeedbackBinding
import com.wallpaper.myViewmodel.UserInfoViewModel
import com.wallpaper.mylibrary.BaseActivity
import com.wallpaper.mylibrary.utils.GetFileFromUri
import com.wallpaper.mylibrary.utils.GetStatusBarHeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UserFeedbackActivity : BaseActivity<ActivityUserFeedbackBinding>() {
    private lateinit var mTvFeedback: TextView
    private lateinit var mTvSuggestions: TextView
    private lateinit var mEtUserFeedback: EditText
    private lateinit var mBtnCommit: Button
    private lateinit var mTvNumber: TextView
    private lateinit var mLlFeedbackImg: LinearLayout
    private lateinit var mTvGetImage: TextView
    private lateinit var viewModel: UserInfoViewModel
    private var file: File? = null
    private var body: MultipartBody.Part? = null
    private var feedbackType: Int = 1

    private var listImage: MutableList<MultipartBody.Part> = mutableListOf()
    private var dataImage: String = ""
    private val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 123
    private lateinit var lottie: LottieAnimationView

    private val cropCallback: PickCallback = object : PickCallback() {
        override fun onPickImage(imageUri: Uri?) {
            super.onPickImage(imageUri)
            log("$imageUri")
            addImage(imageUri)
        }
    }

    override fun getView(): ActivityUserFeedbackBinding {
        return ActivityUserFeedbackBinding.inflate(layoutInflater)
    }

    override fun init() {
        initView()
        initData()
        initEvent()
    }

    private fun initView() {
        GetStatusBarHeight.setBarHeight(vb.textView21, application, 24, 0, 0, 0)
        mTvFeedback = vb.tvFeedback
        mTvSuggestions = vb.tvSuggestions
        mEtUserFeedback = vb.etUserFeedback
        mBtnCommit = vb.btnCommit
        mLlFeedbackImg = vb.llFeedbackImg
        mTvNumber = vb.tvNum
        mTvGetImage = vb.tvGetImage
        lottie = vb.lottie
    }

    private fun initData() {
        viewModel = ViewModelProvider(this)[UserInfoViewModel::class.java]

        mEtUserFeedback.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mTvNumber.text = "${s?.length}/200"
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initEvent() {
        mTvFeedback.setOnClickListener {
            mTvFeedback.background = getDrawable(R.drawable.user_feedback_selected_border)
            mTvFeedback.setTextColor(getColor(R.color.white))
            mTvSuggestions.background = getDrawable(R.drawable.user_feedback_border)
            mTvSuggestions.setTextColor(getColor(R.color._222222))
            feedbackType = 1
        }
        mTvSuggestions.setOnClickListener {
            mTvSuggestions.background = getDrawable(R.drawable.user_feedback_selected_border)
            mTvSuggestions.setTextColor(getColor(R.color.white))
            mTvFeedback.background = getDrawable(R.drawable.user_feedback_border)
            mTvFeedback.setTextColor(getColor(R.color._222222))
            feedbackType = 2
        }
        mTvGetImage.setOnClickListener {
            checkStoragePermission()
        }
        //提交反馈
        mBtnCommit.setOnClickListener {
            val feedbackContent = mEtUserFeedback.text.toString()
            if (feedbackContent.isEmpty()) {
                toast(getString(R.string.fillItout_completely))
                return@setOnClickListener
            }
            toast(getString(R.string.committing))
            lottie.visibility = View.VISIBLE
            mBtnCommit.isEnabled = false
            mBtnCommit.text = ""
            //反馈图片
            if (file == null) {
                CoroutineScope(Dispatchers.IO).launch {
                    userFeedback(feedbackType, feedbackContent, dataImage, getToken())
                }
                return@setOnClickListener
            }
            file?.let { it1 -> viewModel.setUserImg(it1.name, listImage, getToken()) }
            viewModel.mUserUpdate.observe(this) { it ->
                val data = it.data.toMutableList()
                log("$data")
                CoroutineScope(Dispatchers.IO).launch {
                    repeat(data.size) {
                        dataImage += if (it == data.size - 1) {
                            data[it]
                        } else {
                            "${data[it]};"
                        }
                    }
                    log("data-->$data")
                    //反馈
                    userFeedback(feedbackType, feedbackContent, dataImage, getToken())
                }
            }
        }
    }

    private suspend fun userFeedback(
        feedbackType: Int,
        feedbackContent: String,
        feedbackImgList: String? = null,
        token: String? = null
    ) {
        withContext(Dispatchers.IO) {
            val apiGetService = RetrofitUtil.getPostCall(token)
            val categoryListAllCall =
                apiGetService.userFeedback(
                    AppUrl.userFeedBack,
                    UserFeedBackRequestData(feedbackType, feedbackContent, feedbackImgList)
                )
            withContext(Dispatchers.Main) {
                categoryListAllCall?.enqueue(object : Callback<SuccessBean?> {
                    override fun onResponse(
                        call: Call<SuccessBean?>,
                        response: Response<SuccessBean?>
                    ) {
                        log("userFeedback-->${response.code()}")
                        if (response.body()?.data == true) {
                            mEtUserFeedback.text.clear()
                            toast(getString(R.string.feedback_successful))
                        }
                    }

                    override fun onFailure(call: Call<SuccessBean?>, t: Throwable) {
                        toastError()
                    }
                })
            }
            runOnUiThread {
                listImage.clear()
                if (mLlFeedbackImg.childCount > 0) {
                    mLlFeedbackImg.removeAllViews()
                }
                mTvGetImage.visibility = View.VISIBLE
                vb.ivGetImage.visibility = View.VISIBLE
                dataImage = ""
                mBtnCommit.text = getString(R.string.user_feedback_btn)
                lottie.visibility = View.GONE
                mBtnCommit.isEnabled = true
            }
        }
    }

    //添加图片
    fun addImage(imageUri: Uri?) {
        file = GetFileFromUri.getFileFromUri(application, imageUri)
        val requestFile =
            file?.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (requestFile != null) {
            body = MultipartBody.Part.createFormData("files", file?.name, requestFile)
            listImage.add(body!!)
            log("$listImage")
            if (listImage.size == 3) {
                mTvGetImage.visibility = View.GONE
                vb.ivGetImage.visibility = View.GONE
            }
            mTvGetImage.text = "${listImage.size}/3"
        }
        val view = layoutInflater.inflate(R.layout.user_feedback_image_item, null)
        mLlFeedbackImg.addView(view)
        val feedbackImg = view.findViewById<ImageView>(R.id.iv_image)
        feedbackImg.setImageURI(imageUri)
        feedbackImg.tag = body
        val feedbackCloseImg = view.findViewById<ImageView>(R.id.iv_feedback_item_close)
        //删除图片
        feedbackCloseImg.setOnClickListener {
            log("${feedbackImg.tag}")
            listImage.remove(feedbackImg.tag)
            mTvGetImage.text = "${listImage.size}/3"
            if (listImage.size < 3) {
                mTvGetImage.visibility = View.VISIBLE
                vb.ivGetImage.visibility = View.VISIBLE
            }
            mLlFeedbackImg.removeView(view)
            log("$listImage")
        }
    }

    //        override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//            log("onRequestPermissionsResult-->$requestCode")
//        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // 权限已授予，执行获取图片的逻辑
//                ImagePicker.getInstance().onRequestPermissionsResult(this,requestCode, permissions, grantResults)
//            } else {
//                // 权限被拒绝，可以显示一个提示或者禁用相关功能
//                toast("未获得权限")
//            }
//        }
//    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        log("onActivityResult-->$requestCode,$data")
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_CODE || requestCode == 200) {
            log("$data")
            // 权限已授予，执行获取图片的逻辑
            ImagePicker.getInstance().onActivityResult(this, requestCode, resultCode, data)
        } else {
//                 权限被拒绝，可以显示一个提示或者禁用相关功能
            toast(getString(R.string.permission_denied))
        }
    }

    fun back(view: View) {
        finish()
    }

    // 检查是否已经获取了读取存储权限
    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // 如果没有权限，请求权限
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_PERMISSION_CODE
            )
        } else {
            // 如果已经有权限，执行获取图片的逻辑
            ImagePicker.getInstance()
                .startGallery(this@UserFeedbackActivity, false, cropCallback)
        }
    }
}