package com.gayathri.videogallery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gayathri.videogallery.*
import com.gayathri.videogallery.`interface`.ItemClickListener
import com.gayathri.videogallery.model.VideoModel
import kotlinx.android.synthetic.main.layout_video.view.*
import kotlin.properties.Delegates

class VideoAdapter(private val clickListener: ItemClickListener) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    var itemList: List<VideoModel> by Delegates.observable(listOf()) { _, _, _ -> notifyDataSetChanged() }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
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
            itemView.setOnClickListener {
                clickListener.onItemClick(video, absoluteAdapterPosition)
            }
        }
    }
}
