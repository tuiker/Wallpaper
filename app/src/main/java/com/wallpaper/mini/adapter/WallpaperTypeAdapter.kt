package com.wallpaper.mini.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.myretrofit2.bean.ListData
import com.wallpaper.mini.databinding.WallpaperTypeItemBinding
import com.wallpaper.mylibrary.utils.MyGlide
import com.wallpaper.mylibrary.utils.StringToList


class WallpaperTypeAdapter(
    private val clickListener: ItemClickListener
) : BaseQuickAdapter<ListData, WallpaperTypeAdapter.VH>() {
    interface ItemClickListener {
        fun onItemClicked(position: Int)
    }

    class VH(
        parent: ViewGroup,
        val binding: WallpaperTypeItemBinding = WallpaperTypeItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: VH,
        @SuppressLint("RecyclerView") position: Int,
        item: ListData?
    ) {
        if (item != null) {
            holder.binding.tvWallpaperAuthor.text = item.name
            holder.binding.tvWallpaperDes.text = item.details
            holder.binding.tvDownloadNumber.text = item.downloadNum.toString()
            val imgUrlList = StringToList.stringToList(item.imgUrlList)
            holder.binding.htWallpaperItem.removeAllViews()
            imgUrlList.forEach { img ->
                val imageView = buildImageView()
                MyGlide.setGlideImage(context, img, imageView, radius = 12)
                holder.binding.htWallpaperItem.addView(imageView)
                imageView.setOnClickListener {
                    clickListener.onItemClicked(item.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        // 返回一个 ViewHolder
        return VH(parent)
    }

    private fun buildImageView(): ImageView {
        val imageView = ImageView(context)
        val widthInDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            124f,  // 你想要设置的宽度（dp）
            context.resources.displayMetrics
        ).toInt()
        val heightInDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            168f,  // 你想要设置的宽度（dp）
            context.resources.displayMetrics
        ).toInt()
        imageView.layoutParams = ViewGroup.LayoutParams(
            widthInDp,
            heightInDp
        )
        imageView.apply {
            setPadding(32, 0, 0, 0)
            scaleType = ImageView.ScaleType.CENTER
            adjustViewBounds = true
        }
        return imageView
    }

}