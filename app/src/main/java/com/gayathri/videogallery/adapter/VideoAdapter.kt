package com.gayathri.evaluationsample.presentation.adapter

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gayathri.videogallery.*
import kotlinx.android.synthetic.main.layout_video.view.*
import kotlin.properties.Delegates

class PopularNewsAdapter(private val clickListener: ItemClickListener) :
    RecyclerView.Adapter<PopularNewsAdapter.VideoViewHolder>() {

    var itemList: List<VideoModel> by Delegates.observable(listOf()) { _, _, _ -> notifyDataSetChanged() }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PopularNewsAdapter.VideoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: PopularNewsAdapter.VideoViewHolder, position: Int) {
        holder.bindData(itemList[position])
    }

    inner class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(video: VideoModel) {
            video.thumnail?.let { url ->
                itemView.ivImage.setImage(url)
            }
            itemView.tvTitle.text = video.title
            video.createdDate?.let { date ->
                itemView.tvDate.text = date.getDateTime()
            }
//            itemView.tvPopularNewsChannelName.text = newsArticle.source.name
            itemView.setOnClickListener {
                clickListener.onItemClick(video, absoluteAdapterPosition)
            }
        }
    }
}

private fun ImageView.setImage(bitmap: Bitmap) {
    Glide.with(this).load(bitmap).fitCenter().into(ivImage)
}
