package com.wallpaper.mini

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.graphics.Color
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.myretrofit2.RetrofitUtil
import com.myretrofit2.api.AppUrl
import com.myretrofit2.bean.SendSmsBean
import com.myretrofit2.bean.UserLoginBean
import com.myretrofit2.bean.UserLoginRequestData
import com.wallpaper.mini.databinding.ActivityMainBinding
import com.wallpaper.mylibrary.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var mEdTelephone: EditText
    private lateinit var mIvClose: ImageView
    private lateinit var mEdCaptcha: EditText
    private lateinit var mTvGetCaptcha: TextView
    private lateinit var mBtnLogin: Button
    private lateinit var inputTelephone: String
    private lateinit var mImgAgree: ImageView
    private var isAgree: Boolean = true

    //倒计时
    private lateinit var countdownTimer: CountDownTimer
    private val totalTimeInMillis: Long = 60000 // 1 分钟，以毫秒为单位
    private lateinit var coroutineScopeIo: CoroutineScope
    override fun getView(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun init() {
        coroutineScopeIo = CoroutineScope(Dispatchers.IO)
        initView()
        initData()
        initEvent()
    }

    private fun initView() {
        mEdTelephone = vb.edTelephone
        mIvClose = vb.ivClose
        mEdCaptcha = vb.edCaptcha
        mTvGetCaptcha = vb.tvGetCaptcha
        mBtnLogin = vb.btnLogin
        mImgAgree = vb.imgAgree
        mIvClose.visibility = View.GONE
        mTvGetCaptcha.isClickable = false
    }

    private fun initData() {
        mEdTelephone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    mTvGetCaptcha.isClickable = false
                    mTvGetCaptcha.setTextColor(Color.parseColor("#C8CACC"))
                    mIvClose.visibility = View.GONE
                } else {
                    mTvGetCaptcha.setTextColor(getColor(R.color._222222))
                    mIvClose.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun initEvent() {
        //清除手机号码
        mIvClose.setOnClickListener {
            mEdTelephone.text.clear()
        }
        //获取验证码
        mTvGetCaptcha.setOnClickListener {
            inputTelephone = mEdTelephone.text.toString()
            log(inputTelephone)
            if (inputTelephone.isEmpty()) {
//                pleaseEnterYourPhoneNumberFirst
                toast(getString(R.string.enter_phonenumber))
            } else {
                sendUserSms(inputTelephone)
                getCaptcha()
            }
        }
        mBtnLogin.setOnClickListener {
            if (isAgree) {
                val telephone = mEdTelephone.text.toString()
                val captcha = mEdCaptcha.text.toString()
                if (captcha.isNotEmpty() && telephone.isNotEmpty()){
                    vb.lottie.visibility = View.VISIBLE
                    mBtnLogin.isEnabled = false
                    mBtnLogin.text = ""
                    userToLogin(telephone, captcha)
                }
            } else {
                toast(getString(R.string.check_agreement))
            }
        }
        mImgAgree.setOnClickListener {
            isAgree = !isAgree
            if (isAgree) {
                mImgAgree.setImageResource(R.drawable.login_agree)
            } else {
                mImgAgree.setImageResource(R.drawable.login_no_agree)
            }
        }
        vb.privacypolicy.setOnClickListener {
            toPage(PrivacyPolicyActivity::class.java)
        }
    }

    private fun userToLogin(telephone: String, captcha: String) {
        coroutineScopeIo.launch {
            val apiGetService = RetrofitUtil.getPostCall()
            val userLogin =
                apiGetService.userLogin(AppUrl.userLogin, UserLoginRequestData(telephone, captcha))
            withContext(Dispatchers.Main) {
                userLogin?.enqueue(object : Callback<UserLoginBean?> {
                    override fun onResponse(
                        call: Call<UserLoginBean?>,
                        response: Response<UserLoginBean?>
                    ) {
                        if (response.body() != null && response.isSuccessful) {
                            log("${response.body()}")
                            val loginData = response.body()!!.data
                            vb.lottie.visibility = View.GONE
                            mBtnLogin.isEnabled = true
                            mBtnLogin.text = getString(R.string.login_btn)
                            sharedPreferencesInstance().saveString("token", loginData.token)
                            log(loginData.token)
                            log(sharedPreferencesInstance().getString("token", "11111111"))
                            log("${getRunningActivityCount()}")
                            if (getRunningActivityCount() > 2) {
                                finish()
                            } else {
                                toPageFinish(HomeActivity::class.java)
                            }
                        }
                    }

                    override fun onFailure(call: Call<UserLoginBean?>, t: Throwable) {
                        toastError()
                    }
                })
            }
        }
    }

    fun getRunningActivityCount(): Int {
        val activityManager =
            application.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val taskInfoList = activityManager.getRunningTasks(Int.MAX_VALUE)

        var activityCount = 0
        for (taskInfo in taskInfoList) {
            activityCount += taskInfo.numActivities
        }
        return activityCount
    }

    private fun sendUserSms(inputTelephone: String) {
        coroutineScopeIo.launch {
            val apiGetService = RetrofitUtil.getGetCall()
            val sendSms =
                apiGetService.sendRegisterOrLoginSms(AppUrl.sendSms, inputTelephone)
            withContext(Dispatchers.Main) {
                sendSms?.enqueue(object : Callback<SendSmsBean?> {
                    override fun onResponse(
                        call: Call<SendSmsBean?>,
                        response: Response<SendSmsBean?>
                    ) {
                        if (response.body() != null && response.isSuccessful) {
                            toast(getString(R.string.sended))
                        }
                    }

                    override fun onFailure(call: Call<SendSmsBean?>, t: Throwable) {
                        toastError()
                    }
                })
            }
        }
    }

    //验证码倒计时
    private fun getCaptcha() {
        countdownTimer = object : CountDownTimer(totalTimeInMillis, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                mTvGetCaptcha.text =
                    "${getString(R.string.login_get_captcha_countdown)}($seconds ${getString(R.string.login_get_captcha_countdown2)})"
                mTvGetCaptcha.isClickable = false
            }

            override fun onFinish() {
                mTvGetCaptcha.text = getString(R.string.login_get_captcha)
                mTvGetCaptcha.isClickable = true
            }
        }
        countdownTimer.start()
    }

}