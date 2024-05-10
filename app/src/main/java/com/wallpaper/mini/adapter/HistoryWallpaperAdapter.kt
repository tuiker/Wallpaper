package com.wallpaper.mini.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.myretrofit2.bean.HistoryDetails
import com.wallpaper.mini.R
import com.wallpaper.mini.SettingWallpaperActivity
import com.wallpaper.mini.databinding.HistoryWallpaperItemBinding
import com.wallpaper.mylibrary.utils.MyGlide

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