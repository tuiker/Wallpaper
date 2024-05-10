package com.wallpaper.mini.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter4.BaseQuickAdapter
import com.myretrofit2.bean.CollectDetails
import com.wallpaper.mini.FavoritesWallpaperActivity
import com.wallpaper.mini.R
import com.wallpaper.mini.databinding.MyWallpaperItemBinding
import com.wallpaper.mylibrary.utils.StringToList
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class MyWallpaperAdapter : BaseQuickAdapter<CollectDetails, MyWallpaperAdapter.VH>() {
    private val cornerTypeList = listOf(
        RoundedCornersTransformation.CornerType.TOP_LEFT,
        RoundedCornersTransformation.CornerType.TOP_RIGHT,
        RoundedCornersTransformation.CornerType.BOTTOM_LEFT,
        RoundedCornersTransformation.CornerType.BOTTOM_RIGHT
    )

    class VH(
        parent: ViewGroup,
        val binding: MyWallpaperItemBinding = MyWallpaperItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    @SuppressLint("NotifyDataSetChanged", "CheckResult")
    override fun onBindViewHolder(
        holder: VH,
        @SuppressLint("RecyclerView") position: Int,
        item: CollectDetails?
    ) {
        val imageViewList = listOf(
            holder.binding.ivWallpaper1,
            holder.binding.ivWallpaper2,
            holder.binding.ivWallpaper3,
            holder.binding.ivWallpaper4
        )
        Log.e("---TAG---", "onBindViewHolder: ${item?.imgUrlList}" )
        if (item?.imgUrlList != null) {
            val imgList = StringToList.stringToList(item.imgUrlList)
            for (i in imgList.indices) {
                if (i >= 4) return
                glideSetImage(imgList[i], imageViewList[i], cornerTypeList[i])
                imageViewList[i].visibility = View.VISIBLE
            }
            holder.binding.tvTitle.text = item.title
            holder.binding.tvUsername.text = item.describe
        } else {
            Glide.with(context)
                .load(R.drawable.delete_img)
                .override(76 * 3, 76 * 3)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(multiTransformation(RoundedCornersTransformation.CornerType.TOP_LEFT))
                .into(holder.binding.ivWallpaper1)
        }
        holder.itemView.setOnClickListener {
            Log.e("---TAG---", "onBindViewHolder: ${item?.id}")
            context.startActivity(
                Intent(
                    context,
                    FavoritesWallpaperActivity::class.java
                ).putExtra("favoritesWallpaper", item?.id)
            )
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        // 返回一个 ViewHolder
        return VH(parent)
    }

    private fun glideSetImage(
        url: String,
        ivWallpaper2: ImageView,
        cornerType: RoundedCornersTransformation.CornerType
    ) {
        Glide.with(context)
            .load(url)
            .override(76 * 3, 76 * 3)
            .transition(DrawableTransitionOptions.withCrossFade())
            .transform(multiTransformation(cornerType))
            .error(R.drawable.delete_img)
            .into(ivWallpaper2)
    }

    private fun multiTransformation(cornerType: RoundedCornersTransformation.CornerType): MultiTransformation<Bitmap> {
        return MultiTransformation(
            CenterCrop(),
            RoundedCornersTransformation(
                context.resources.getDimensionPixelSize(R.dimen.corner_radius), // 设置圆角半径
                0, // 不设置任何圆角的角
                cornerType // 选择要设置圆角的角
            ),
        )
    }
}