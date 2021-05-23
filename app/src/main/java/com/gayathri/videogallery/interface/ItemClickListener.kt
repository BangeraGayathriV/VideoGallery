package com.gayathri.videogallery.`interface`

import com.gayathri.videogallery.model.VideoModel

interface ItemClickListener {
    fun onItemClick(item: VideoModel, position: Int)
}
