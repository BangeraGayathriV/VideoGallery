package com.gayathri.videogallery.model

import android.content.ContentResolver
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.gayathri.videogallery.getThumbnailBitmap
import java.io.File
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
) {
    companion object {
        fun create(cursor: Cursor, contentResolver: ContentResolver): VideoModel =
            VideoModel(
                title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)) ?: "",
                description =
                cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DESCRIPTION)) ?: "",
                displayName =
                cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)) ?: "",
                album =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ALBUM))
                } else "",
                createdDate =
                cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)),
                videoUrl = Uri.fromFile(
                    File(
                        cursor.getString(
                            cursor.getColumnIndex(
                                MediaStore.Video.Media.DATA
                            )
                        )
                    )
                ).toString(),
                thumnail = getThumbnailBitmap(
                    contentResolver,
                    cursor
                ),
                id = cursor.position
            )
    }
}
