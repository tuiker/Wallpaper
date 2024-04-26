package com.wallpaper.bottomDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wallpaper.R

class DialogTakePhotosFragment(
    val onClickListener: OnClickListener,
) : BottomSheetDialogFragment() {
    interface OnClickListener {
        fun onCameraClicked()
        fun onGalleryClicked()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.set_avatar_bottomdialog, container, false)
        view.findViewById<TextView>(R.id.bottom_dialog_cancel).setOnClickListener {
            dismiss()
        }
        view.findViewById<TextView>(R.id.tv_camera)
            .setOnClickListener {
                onClickListener.onCameraClicked()
                dismiss()
            }
        view.findViewById<TextView>(R.id.tv_gallery)
            .setOnClickListener {
                onClickListener.onGalleryClicked()
                dismiss()
            }

        return view
    }
}