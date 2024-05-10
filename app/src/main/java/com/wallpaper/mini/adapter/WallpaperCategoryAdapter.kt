package com.wallpaper.mini.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.myretrofit2.bean.FavoritesData
import com.wallpaper.mini.R
import com.wallpaper.mini.databinding.WallpaperCategoryItemBinding

class WallpaperCategoryAdapter(val itemClickListener: ItemClickListener) :
    BaseQuickAdapter<FavoritesData, WallpaperCategoryAdapter.VH>() {
    private var selectedItem: Int = -1

    interface ItemClickListener {
        fun onItemClicked(favoritesData: FavoritesData)
    }

    class VH(
        parent: ViewGroup,
        val binding: WallpaperCategoryItemBinding = WallpaperCategoryItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: VH,
        @SuppressLint("RecyclerView") position: Int,
        item: FavoritesData?
    ) {
        if (item != null) {
            holder.binding.tvCategoryTitle.text = item.title
            holder.itemView.setOnClickListener {
                selectedItem = position
                notifyDataSetChanged()
                itemClickListener.onItemClicked(item)
            }
        }
        // 根据项的状态设置颜色
        if (position == selectedItem) {
            holder.itemView.setBackgroundResource(R.drawable.wallpaper_category_item_border_selected)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.wallpaper_category_item_border)
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        // 返回一个 ViewHolder
        return VH(parent)
    }
}