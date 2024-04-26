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

class TypeLoadMoreAdapter(val context:Context): TrailingLoadStateAdapter<TypeLoadMoreAdapter.LoadMoreVH>() {
    class LoadMoreVH(val viewBinding: LoadMoreBinding) : RecyclerView.ViewHolder(viewBinding.root)

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