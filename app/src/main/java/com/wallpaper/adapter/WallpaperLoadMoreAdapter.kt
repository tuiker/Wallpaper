package com.wallpaper.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chad.library.adapter4.loadState.LoadState
import com.chad.library.adapter4.loadState.trailing.TrailingLoadStateAdapter
import com.wallpaper.R
import com.wallpaper.databinding.LoadMoreBinding

class WallpaperLoadMoreAdapter(val context: Context) :
    TrailingLoadStateAdapter<WallpaperLoadMoreAdapter.LoadMoreVH>() {
    class LoadMoreVH(val viewBinding: LoadMoreBinding) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onViewAttachedToWindow(holder: LoadMoreVH) {
        super.onViewAttachedToWindow(holder)
        val layoutParams =
            holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
        layoutParams.isFullSpan = true
        holder.itemView.layoutParams = layoutParams
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadMoreVH {
        // 创建你自己的 UI 布局
        val viewBinding =
            LoadMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadMoreVH(viewBinding).apply {
            viewBinding.tvTip.setOnClickListener {
                // 加载更多，手动点击事件
                invokeLoadMore()
            }
        }
    }

    override fun onBindViewHolder(holder: LoadMoreVH, loadState: LoadState) {
        Log.e("---TAG---", "onBindViewHolder: $loadState")
        // 根据加载状态，来自定义你的 UI 界面
        when (loadState) {
            is LoadState.NotLoading -> {
                if (loadState.endOfPaginationReached) {
                    holder.viewBinding.tvTip.text = context.getString(R.string.load_all)
                } else {
                    holder.viewBinding.tvTip.text = context.getString(R.string.click_load_more)
                }
            }

            is LoadState.Loading -> {
                holder.viewBinding.tvTip.text = context.getString(R.string.loading)
            }

            is LoadState.Error -> {
                holder.viewBinding.tvTip.text = context.getString(R.string.network_error)
            }

            is LoadState.None -> {
                holder.viewBinding.tvTip.text = context.getString(R.string.click_load_more)
            }
        }
    }

}