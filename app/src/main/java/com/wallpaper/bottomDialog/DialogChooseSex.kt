package com.wallpaper.bottomDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wallpaper.R
import com.wallpaper.myViewmodel.UserInfoViewModel
import kotlin.properties.Delegates

class DialogChooseSex : BottomSheetDialogFragment() {
    private lateinit var viewModel: UserInfoViewModel
    private var sex = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.choose_sex_bottomdialog, container, false)
        viewModel = ViewModelProvider(requireActivity())[UserInfoViewModel::class.java]

        view.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dismiss()
        }
        view.findViewById<TextView>(R.id.tv_sure).setOnClickListener {
            viewModel.mUserSex.value = sex
            dismiss()
        }
        val boy = view.findViewById<TextView>(R.id.tv_boy)
        val woman = view.findViewById<TextView>(R.id.tv_woman)
        boy.setOnClickListener {
            woman.textSize = 15f
            boy.textSize = 18f
            sex = 1
        }
        woman.setOnClickListener {
            boy.textSize = 15f
            woman.textSize = 18f
            sex = 2
        }

        return view
    }
}