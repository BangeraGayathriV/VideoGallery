package com.gayathri.videogallery

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_video.view.*
import java.text.SimpleDateFormat
import java.util.*

fun ImageView.setImage(bitmap: Bitmap) {
    Glide.with(this).load(bitmap).fitCenter().into(ivImage)
}

@SuppressLint("SimpleDateFormat")
fun Long.getDateTime(): String? {
    try {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val netDate = Date(this * 1000)
        return sdf.format(netDate)
    } catch (e: Exception) {
        return e.toString()
    }
}

fun getThumbnailBitmap(
    contentResolver: ContentResolver,
    cursor: Cursor
): Bitmap? {
    val option = BitmapFactory.Options()
    option.inSampleSize = 1
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentResolver.loadThumbnail(
            Uri.parse(
                cursor.getString(
                    cursor.getColumnIndex(
                        MediaStore.Images.Media.DATA
                    )
                )
            ), Size(WIDTH, HEIGHT), null
        )
    } else {
        val picturePath =
            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        ThumbnailUtils.createVideoThumbnail(
            picturePath,
            MediaStore.Video.Thumbnails.MICRO_KIND
        )
    }
}

private const val HEIGHT = 50
private const val WIDTH = 50
