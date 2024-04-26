package com.wallpaper.mylibrary.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.wallpaper.mylibrary.R
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class MyGlide {
    companion object {
        fun setGlideImage(context: Context, url: String?, view: ImageView, radius: Int? = 1,errorImg:Int?=R.drawable.delete_wallpaper) {
            val requestOptions = RequestOptions().transform(RoundedCorners(radius!!)) // 设置圆角半径
            Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(errorImg)
                .transform(
                    MultiTransformation(
                        CenterCrop(),
                        RoundedCornersTransformation(
                            radius, // 设置圆角半径
                            0, // 不设置任何圆角的角
                            RoundedCornersTransformation.CornerType.ALL // 选择要设置圆角的角
                        ),
                    )
                )
                .into(view)
        }
        fun setGlideImage(context: Context, url: Int?, view: ImageView, radius: Int? = 1) {
            val requestOptions = RequestOptions().transform(RoundedCorners(radius!!)) // 设置圆角半径
            Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(
                    MultiTransformation(
                        CenterCrop(),
                        RoundedCornersTransformation(
                            radius, // 设置圆角半径
                            0, // 不设置任何圆角的角
                            RoundedCornersTransformation.CornerType.ALL // 选择要设置圆角的角
                        ),
                    )
                )
                .into(view)
        }

        fun setGlideImage(context: Context, url: Uri?, view: ImageView, radius: Int? = 1) {
            val requestOptions = RequestOptions().transform(RoundedCorners(radius!!)) // 设置圆角半径
            Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(
                    MultiTransformation(
                        CenterCrop(),
                        RoundedCornersTransformation(
                            radius, // 设置圆角半径
                            0, // 不设置任何圆角的角
                            RoundedCornersTransformation.CornerType.ALL // 选择要设置圆角的角
                        ),
                    )
                )
                .into(view)
        }

        fun setGlideImage(
            context: Context,
            url: String?,
            view: ImageView,
            resourceId: Int,
            radius: Int? = 1
        ) {
            Glide.with(context)
                .load(url)
                .error(resourceId)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(
                    MultiTransformation(
                        CenterCrop(),
                        RoundedCornersTransformation(
                            radius!!, // 设置圆角半径
                            0, // 不设置任何圆角的角
                            RoundedCornersTransformation.CornerType.ALL // 选择要设置圆角的角
                        ),
                    )
                )
                .into(view)
        }
    }

}