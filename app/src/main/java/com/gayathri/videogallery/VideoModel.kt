package com.gayathri.videogallery

import android.graphics.Bitmap
import java.net.URI

data class VideoModel(
    val title: String = "",
    val description: String = "",
    val displayName: String = "",
    val album: String = "",
    val thumnail: Bitmap? = null,
    val imageUri: URI? = null,
    val createdDate: Long? = null,
    val videoUrl: String? = null,
    val id: Int
)
