package com.gayathri.videogallery

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore

object LocalStorageVideos {
    fun getLocalStorageVideos(contentResolver: ContentResolver): Cursor? {
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        return contentResolver.query(uri, null, null, null, null)
    }
}
