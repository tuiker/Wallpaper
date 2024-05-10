package com.wallpaper.mini.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.myretrofit2.bean.ListData
import com.wallpaper.mini.R
import com.wallpaper.mini.SettingWallpaperActivity
import com.wallpaper.mini.databinding.WallpaperAdItemBinding
import com.wallpaper.mini.databinding.WallpaperListItemBinding
import com.wallpaper.mylibrary.utils.MyGlide
import com.wallpaper.mylibrary.utils.StringToList

class WallpaperAdapter(private val clickListener: ItemClickListener) :
    BaseMultiItemAdapter<ListData>() {
    interface ItemClickListener {
        fun onItemClicked(listData: ListData)
    }

    // 类型 1 的 WALLPAPER_TYPE
    class ItemVH(val viewBinding: WallpaperListItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    // 类型 2 的 viewholder
    class HeaderVH(val viewBinding: WallpaperAdItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    init {
        addItemType(WALLPAPER_TYPE, object : OnMultiItemAdapterListener<ListData, ItemVH> { // 类型 1
            override fun onCreate(context: Context, parent: ViewGroup, viewType: Int): ItemVH {
                // 创建 viewholder
                val viewBinding =
                    WallpaperListItemBinding.inflate(LayoutInflater.from(context), parent, false)
                return ItemVH(viewBinding)
            }

            override fun onBind(holder: ItemVH, position: Int, item: ListData?) {
                if (item != null) {
                    val ivWallpaper = holder.viewBinding.ivWallpaper
                    val imgUrlList = StringToList.stringToList(item.imgUrlList)
                    MyGlide.setGlideImage(
                        context,
                        imgUrlList[0],
                        ivWallpaper,
                        radius = 16,
                        R.drawable.delete_wallpaper
                    )
                    ivWallpaper.visibility = View.VISIBLE
                    holder.viewBinding.tvAuthor.text = item.nickname
                    holder.viewBinding.tvTitle.text = item.name
                    holder.viewBinding.tvDownloadNumber.text = "${item.downloadNum}"

                }
                // 绑定 item 数据
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

            override fun isFullSpanItem(itemType: Int): Boolean {
                // 使用GridLayoutManager时，此类型的 item 是否是满跨度
                return false
            }
        }).addItemType(
            AD_TYPE,
            object : OnMultiItemAdapterListener<ListData, HeaderVH> { // 类型 2
                override fun onCreate(
                    context: Context,
                    parent: ViewGroup,
                    viewType: Int
                ): HeaderVH {
                    // 创建 viewholder
                    val viewBinding =
                        WallpaperAdItemBinding.inflate(LayoutInflater.from(context), parent, false)
                    return HeaderVH(viewBinding)
                }

                @OptIn(UnstableApi::class)
                override fun onBind(holder: HeaderVH, position: Int, item: ListData?) {
                    if (item != null) {
                        holder.viewBinding.tvAdTitle.text = "${item.advName}"
                        if (item.contentsType.toString().toDouble().toInt() == 1) {
                            holder.viewBinding.ivAdWallpaper.visibility = View.VISIBLE
                            MyGlide.setGlideImage(
                                context,
                                item.advContents.toString(),
                                holder.viewBinding.ivAdWallpaper,
                                radius = 16,
                                R.drawable.delete_wallpaper
                            )
                        } else {
                            val playerView = holder.viewBinding.playerView
                            playerView.visibility = View.VISIBLE
                            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            val player = ExoPlayer.Builder(context).build()
                            playerView.player = player
                            playerView.visibility = View.VISIBLE
                            val mediaItem = MediaItem.Builder()
                                .setUri(Uri.parse(item.advContents.toString()))
                                .build()
                            player.setMediaItem(mediaItem)
                            player.prepare()
                            player.play()
                            player.addListener(object :Listener{
                                override fun onPlaybackStateChanged(playbackState: Int) {
                                    super.onPlaybackStateChanged(playbackState)
                                    if (playbackState == Player.STATE_ENDED) {
                                        // 播放完成后重新开始播放视频
                                        player.seekTo(0)
                                        player.play()
                                    }
                                }
                            })
                        }
                        holder.itemView.setOnClickListener {
                            clickListener.onItemClicked(item)
                        }
                    }
                }

                override fun isFullSpanItem(itemType: Int): Boolean {
                    return false
                }

            })
            .onItemViewType { position, list -> // 根据数据，返回对应的 ItemViewType
                if (list[position].isAdv == true) {
                    //广告
                    AD_TYPE
                } else {
                    //壁纸
                    WALLPAPER_TYPE
                }
            }
    }

    companion object {
        private const val WALLPAPER_TYPE = 0
        private const val AD_TYPE = 1
    }
}