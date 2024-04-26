package com.wallpaper

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.github.gzuliyujiang.imagepicker.ActivityBuilder
import com.github.gzuliyujiang.imagepicker.CropImageView
import com.github.gzuliyujiang.imagepicker.ImagePicker
import com.github.gzuliyujiang.imagepicker.PickCallback
import com.myretrofit2.RetrofitUtil
import com.myretrofit2.api.AppUrl
import com.myretrofit2.bean.SuccessBean
import com.myretrofit2.bean.UserData
import com.myretrofit2.bean.UserUpdateRequestData
import com.wallpaper.bottomDialog.DialogChooseBirthday
import com.wallpaper.bottomDialog.DialogChooseSex
import com.wallpaper.bottomDialog.DialogTakePhotosFragment
import com.wallpaper.databinding.ActivityUserEditBinding
import com.wallpaper.myView.ImageAvatar
import com.wallpaper.myViewmodel.UserInfoViewModel
import com.wallpaper.mylibrary.BaseActivity
import com.wallpaper.mylibrary.utils.GetFileFromUri
import com.wallpaper.mylibrary.utils.GetStatusBarHeight
import com.wallpaper.mylibrary.utils.MyGlide
import com.wallpaper.mylibrary.utils.StringToEditable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ConnectionSpec
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class UserEditActivity : BaseActivity<ActivityUserEditBinding>() {
    private val CAMERA_PERMISSION_CODE = 100
    private val STORAGE_PERMISSION_CODE = 101
    private lateinit var viewModel: UserInfoViewModel
    private lateinit var mIvSetAvatar: ImageView
    private lateinit var mIvCamera: ImageView
    private lateinit var mEtUserBirthday: EditText
    private lateinit var mEtUserSex: EditText
    private lateinit var mEtUserTitle: EditText
    private lateinit var mEtUserSign: EditText
    private var listImage: MutableList<MultipartBody.Part> = mutableListOf()
    private val mUserUpdateRequestData: UserUpdateRequestData = UserUpdateRequestData()
    private val cropCallback: PickCallback = object : PickCallback() {
        override fun onPermissionDenied(permissions: Array<String?>?, message: String?) {
            toast(getString(R.string.permission_denied))
        }

        override fun cropConfig(builder: ActivityBuilder) {
            builder.setMultiTouchEnabled(true).setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setCropShape(CropImageView.CropShape.OVAL).setRequestedSize(400, 400)
                .setFixAspectRatio(true).setAspectRatio(1, 1)
        }

        override fun onCropImage(imageUri: Uri?) {
            log(imageUri.toString())
//            mIvSetAvatar.setImageURI(imageUri)
            MyGlide.setGlideImage(application,imageUri,mIvSetAvatar,radius = 200)

            val file = GetFileFromUri.getFileFromUri(application, imageUri)
            val requestFile = file?.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            if (requestFile != null) {
                val body = MultipartBody.Part.createFormData("files", file.name, requestFile)
                listImage.clear()
                listImage.add(body)
                viewModel.setUserImg(file.name, listImage)
            }
        }
    }

    override fun getView(): ActivityUserEditBinding {
        return ActivityUserEditBinding.inflate(layoutInflater)
    }

    override fun init() {
        GetStatusBarHeight.setBarHeight(vb.textView19, application, 0, 16, 0, 0)
        checkCameraPermission()
        initView()
        initData()
        initEvent()
    }

    private fun initView() {
        mIvSetAvatar = vb.ivSetAvatar
        mIvCamera = vb.ivCamera
        mEtUserTitle = vb.etUserTitle
        mEtUserBirthday = vb.etUserBirthday
        mEtUserSex = vb.etUserSex
        mEtUserSign = vb.etUserSign
    }

    private fun initData() {
        viewModel = ViewModelProvider(this)[UserInfoViewModel::class.java]
        viewModel.getUserInfo(getToken())
        viewModel.mUserUpdate.observe(this@UserEditActivity, Observer {
            log("it-->$it")
            if (it != null && it.code != 1000) {
                toastError()
            } else {
                mUserUpdateRequestData.headImg = it.data[0]
            }
        })
        //用户信息
        viewModel.mUserInfo.observe(this, Observer observe@{
            log("用户信息--> $it")
            if (it != null) {
                when (it.code) {
                    1000 -> {
                        val userData = it.data
                        mUserUpdateRequestData.id = userData.id
                        MyGlide.setGlideImage(
                            application,
                            userData.headImg,
                            mIvSetAvatar,
                            R.drawable.set_avatar,
                            200
                        )
                        mUserUpdateRequestData.headImg = userData.headImg
                        val nickName = StringToEditable.stringToEditable(userData.nickname)
                        mEtUserTitle.text = nickName
                        mUserUpdateRequestData.nickname = nickName.toString()
                        if (userData.gender == 1) {
                            mEtUserSex.text = StringToEditable.stringToEditable(getString(R.string.boy))
                            mUserUpdateRequestData.gender = 1
                        } else {
                            mEtUserSex.text = StringToEditable.stringToEditable(getString(R.string.gril))
                            mUserUpdateRequestData.gender = 0
                        }
                        userData.birthday.let { birthday ->
                            mEtUserBirthday.text = StringToEditable.stringToEditable(birthday)
                            mUserUpdateRequestData.birthday = birthday
                        }
                        userData.signature.let { signature ->
                            mEtUserSign.text = StringToEditable.stringToEditable(signature)
                            mUserUpdateRequestData.signature = signature
                        }
                    }

                    401 -> toPageFinish(LoginActivity::class.java)
                    else -> toastError()
                }
            }
        })
        //出生
        viewModel.mUserBirth.observe(this, Observer { date ->
            val birth = StringToEditable.stringToEditable("${date.year}-${date.month}-${date.day}")
            log("$birth")
            mEtUserBirthday.text = birth
            mUserUpdateRequestData.birthday = birth.toString()

        })
        //性别
        viewModel.mUserSex.observe(this, Observer { sex ->
            if (sex == 1) {
                mEtUserSex.text = StringToEditable.stringToEditable(getString(R.string.boy))
                mUserUpdateRequestData.gender = sex
            } else {
                mEtUserSex.text = StringToEditable.stringToEditable(getString(R.string.gril))
                mUserUpdateRequestData.gender = sex
            }
        })
        vb.btnCommit.setOnClickListener {
            val title = mEtUserTitle.text
            if (containsSpecialCharacter(title.toString())) {
                toast(getString(R.string.nicknames_not_specialcharacters))
                return@setOnClickListener
            }
            if (mEtUserSex.text != null && mEtUserBirthday.text != null && mEtUserSign.text != null && mEtUserTitle.text != null) {
                mUserUpdateRequestData.nickname = mEtUserTitle.text.toString().trim()
                mUserUpdateRequestData.signature = mEtUserSign.text.toString().trim()
                vb.lottie.visibility = View.VISIBLE
                vb.btnCommit.apply {
                    isEnabled = false
                    text = ""
                }
                CoroutineScope(Dispatchers.IO).launch {
                    val apiGetService = RetrofitUtil.getPostCall(getToken())
                    val userUpdateCall =
                        apiGetService.userUpdate(AppUrl.userUpdate, mUserUpdateRequestData)
                    withContext(Dispatchers.Main) {
                        userUpdateCall?.enqueue(object : Callback<SuccessBean?> {
                            override fun onResponse(
                                call: Call<SuccessBean?>,
                                response: Response<SuccessBean?>
                            ) {
                                if (response.isSuccessful) {
                                    toast(getString(R.string.save_successful))
                                }
                            }

                            override fun onFailure(call: Call<SuccessBean?>, t: Throwable) {
                                log("${t.message}")
                                toastError()
                            }
                        })
                        vb.lottie.visibility = View.GONE
                        vb.btnCommit.apply {
                            isEnabled = true
                            text =  getString(R.string.confirmyourchanges)
                        }
                    }
                }
            } else {
                toast(getString(R.string.fillItout_completely))
            }
        }
    }

    private suspend fun upDataUserRequest(
        mUserUpdateRequestData: UserUpdateRequestData, token: String? = null
    ): SuccessBean? {
        var mSuccessBean: SuccessBean?
        return withContext(Dispatchers.IO) {
            val apiGetService = RetrofitUtil.getPostCall(token)
            val categoryListAllCall =
                apiGetService.userUpdate(AppUrl.userUpdate, mUserUpdateRequestData)
            mSuccessBean = try {
                val body = categoryListAllCall?.execute()
                if (body != null && body.isSuccessful) {
                    body.body()
                } else {
                    null
                }
            } catch (e: IOException) {
                null
            }
            mSuccessBean
        }
    }

    private fun initEvent() {
        //设置头像
        mIvSetAvatar.setOnClickListener {
            setAvatar()
        }
        mIvSetAvatar.setOnClickListener {
            setAvatar()
        }
        //设置性别
        vb.ivSex.setOnClickListener {
            val bottomSheetFragment = DialogChooseSex()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
        //设置生日
        vb.chooseUserBirthday.setOnClickListener {
            val bottomSheetFragment = DialogChooseBirthday()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
    }

    //匹配字符串中是否包含标点符号或空白字符
    fun containsSpecialCharacter(input: String): Boolean {
        val pattern = Regex("[\\p{P}\\s]") // 匹配标点符号和空白字符
        return pattern.containsMatchIn(input)
    }

    private fun setAvatar() {
        val bottomSheetFragment =
            DialogTakePhotosFragment(object : DialogTakePhotosFragment.OnClickListener {
                override fun onCameraClicked() {
                    ImagePicker.getInstance().startCamera(this@UserEditActivity, true, cropCallback)
                }

                override fun onGalleryClicked() {
                    ImagePicker.getInstance()
                        .startGallery(this@UserEditActivity, true, cropCallback)
                }
            })
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ImagePicker.getInstance().onActivityResult(this, requestCode, resultCode, data);
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.getInstance()
                        .onRequestPermissionsResult(this, requestCode, permissions, grantResults)
                } else {
                    toast(getString(R.string.not_get_permission))
                }
            }

            200 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.getInstance()
                        .onRequestPermissionsResult(this, requestCode, permissions, grantResults)
                } else {
                    toast(getString(R.string.not_get_permission))
                }
            }

            else -> toast(getString(R.string.not_get_permission))
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 如果权限没有被授予，请求权限
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE
            )
        } else {
            // 如果权限已被授予，执行相应操作
            // 例如启动相机或者进行其他相机相关操作
        }
    }

    fun back(view: View) {
        finish()
    }
}