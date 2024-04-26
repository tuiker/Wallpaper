package com.wallpaper.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.chad.library.adapter4.BaseQuickAdapter
import com.myretrofit2.bean.HistoryDetails
import com.myretrofit2.bean.ListData
import com.myretrofit2.bean.WallpaperHomeBean
import com.wallpaper.R
import com.wallpaper.SettingWallpaperActivity
import com.wallpaper.databinding.FavoritesWallpaperItemBinding
import com.wallpaper.databinding.HistoryWallpaperItemBinding
import com.wallpaper.mylibrary.utils.MyGlide
import com.wallpaper.mylibrary.utils.StringToList
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class FavoritesWallpaperAdapter : BaseQuickAdapter<ListData, FavoritesWallpaperAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val binding: FavoritesWallpaperItemBinding = FavoritesWallpaperItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: VH,
        @SuppressLint("RecyclerView") position: Int,
        item: ListData?
    ) {
        Log.e("---TAG---", "onBindViewHolder: ${item != null}")
        Log.e("---TAG---", "onBindViewHolder: ${item?.imgUrlList != null}")
        Log.e("---TAG---", "onBindViewHolder: ${item?.name != null}")
        if (item != null) {
            if (item.imgUrlList != null) {
                val imgList = StringToList.stringToList(item.imgUrlList)
                MyGlide.setGlideImage(
                    context,
                    imgList[0],
                    holder.binding.ivFavorites,
                    radius = 16,
                    R.drawable.delete_wallpaper
                )
                holder.binding.tvHistoryTitle.text=item.name
            } else {
                val requestOptions = RequestOptions().transform(RoundedCorners(16)) // 设置圆角半径
                Glide.with(context)
                    .load(R.drawable.delete_wallpaper)
                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .transform(
                        MultiTransformation(
                            CenterCrop(),
                            RoundedCornersTransformation(
                                16, // 设置圆角半径
                                0, // 不设置任何圆角的角
                                RoundedCornersTransformation.CornerType.ALL // 选择要设置圆角的角
                            ),
                        )
                    )
                    .into(holder.binding.ivFavorites)
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

    override fun getItemCount(items: List<ListData>): Int {
        Log.e("---TAG---", "getItemCount: ${items.size}")
        return super.getItemCount(items)
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        // 返回一个 ViewHolder
        return VH(parent)
    }
}