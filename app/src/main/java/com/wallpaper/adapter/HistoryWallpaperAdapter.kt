package com.wallpaper.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.chad.library.adapter4.BaseQuickAdapter
import com.myretrofit2.bean.HistoryDetails
import com.wallpaper.R
import com.wallpaper.SettingWallpaperActivity
import com.wallpaper.databinding.HistoryWallpaperItemBinding
import com.wallpaper.mylibrary.utils.MyGlide
import com.wallpaper.mylibrary.utils.StringToList
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class HistoryWallpaperAdapter : BaseQuickAdapter<HistoryDetails, HistoryWallpaperAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val binding: HistoryWallpaperItemBinding = HistoryWallpaperItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: VH,
        @SuppressLint("RecyclerView") position: Int,
        item: HistoryDetails?
    ) {
        if (item != null) {
            if (item.id!=0) {
                MyGlide.setGlideImage(
                    context,
                    item.wallpaperDownloadUrl,
                    holder.binding.ivHistory,
                    radius = 16
                )
                holder.binding.tvHistoryTitle.text = item.name
            }else{
                MyGlide.setGlideImage(
                    context,
                    R.drawable.delete_wallpaper,
                    holder.binding.ivHistory,
                    radius = 16
                )
            }
        }
        holder.itemView.setOnClickListener {
            if (item != null) {
                context.startActivity(
                    Intent(
                        context,
                        SettingWallpaperActivity::class.java
                    ).putExtra("wallpaperId", item.id)
                )
            }
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        // 返回一个 ViewHolder
        return VH(parent)
    }
}