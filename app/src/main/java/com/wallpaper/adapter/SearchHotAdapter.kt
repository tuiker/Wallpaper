package com.wallpaper.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myretrofit2.bean.HotSearchListBean
import com.wallpaper.databinding.HotFindItemBinding
import com.wallpaper.databinding.SearchHistoryItemBinding

class SearchHotAdapter(
    val data: HotSearchListBean,
    private val clickListener: AdapterItemClickListener
) :
    RecyclerView.Adapter<SearchHotAdapter.VH>() {
    private val colorList = listOf(
        "#FD4029",
        "#FF6E35",
        "#FFB735",
    )
    interface AdapterItemClickListener {
        fun onItemClicked(position: Int)
    }
    class VH(
        parent: ViewGroup,
        val binding: HotFindItemBinding = HotFindItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.tvSerialNumber.apply {
            setTextColor(Color.parseColor(if (position < 3) colorList[position] else "#808080"))
            text = "${position + 1}"
        }
        holder.binding.tvTitle.text = data.data[position].keyword
        holder.itemView.setOnClickListener {
            clickListener.onItemClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return data.data.size
    }
}