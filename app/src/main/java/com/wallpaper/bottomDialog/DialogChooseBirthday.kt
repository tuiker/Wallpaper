package com.wallpaper.bottomDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.github.gzuliyujiang.wheelpicker.widget.DateWheelLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.myretrofit2.bean.UserDate
import com.wallpaper.R
import com.wallpaper.myViewmodel.UserInfoViewModel
import java.util.Calendar

class DialogChooseBirthday : BottomSheetDialogFragment() {
    private lateinit var viewModel: UserInfoViewModel
    private var mUserDate: UserDate = UserDate()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.choose_user_birthday, container, false)
        view.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dismiss()
        }
        viewModel = ViewModelProvider(requireActivity())[UserInfoViewModel::class.java]
        val wheelLayout = view.findViewById<DateWheelLayout>(R.id.date_birthday)
        setBirthday(wheelLayout)

        view.findViewById<TextView>(R.id.tv_sure).setOnClickListener {
            viewModel.mUserBirth.value = mUserDate
            dismiss()
        }
        return view
    }

    private fun setBirthday(
        wheelLayout: DateWheelLayout,
    ) {
        val time = Calendar.getInstance()
        wheelLayout.setRange(
            DateEntity.target(1900, 1, 31),
            DateEntity.target(2099, 12, 31),
            DateEntity.target(
                time.get(Calendar.YEAR),
                time.get(Calendar.MONTH) + 1,
                time.get(Calendar.DAY_OF_MONTH)
            ),
        )
        mUserDate.year = time.get(Calendar.YEAR)
        if (time.get(Calendar.MONTH) + 1 < 10) {
            mUserDate.month = "0${time.get(Calendar.MONTH) + 1}"
        }else{
            mUserDate.month = "${time.get(Calendar.MONTH) + 1}"
        }
        if (time.get(Calendar.DAY_OF_MONTH) < 10) {
            mUserDate.day = "0${time.get(Calendar.DAY_OF_MONTH)}"
        }else{
            mUserDate.day = "${time.get(Calendar.DAY_OF_MONTH)}"
        }
        wheelLayout.setTextSize(45f)
        wheelLayout.setCyclicEnabled(true)
        wheelLayout.setSelectedTextSize(54f)

        wheelLayout.setOnDateSelectedListener { year, month, day ->
            mUserDate.year = year
            if (month < 10) {
                mUserDate.month = "0$month"
            }else{
                mUserDate.month = "$month"
            }
            if (day < 10) {
                mUserDate.day = "0$day"
            }else{
                mUserDate.day = "$day"
            }
        }
        wheelLayout.setResetWhenLinkage(false)
    }
}