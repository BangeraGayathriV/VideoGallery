package com.gayathri.videogallery.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gayathri.videogallery.VideoModel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.*

class MainViewModel(val context: Context) : ViewModel() {

    val video: LiveData<VideoModel> get() = _video
    val spanCount: LiveData<Int> get() = _spanCount

    val videosList: LiveData<List<VideoModel>> get() = _videos
    val searchResults: LiveData<List<VideoModel>> get() = _searchResults

    var disposable: CompositeDisposable = CompositeDisposable()

    private val _video: MutableLiveData<VideoModel> = MutableLiveData()
    private val _spanCount: MutableLiveData<Int> = MutableLiveData()
    private val _videos: MutableLiveData<List<VideoModel>> = MutableLiveData()
    private val _searchResults: MutableLiveData<List<VideoModel>> = MutableLiveData()


    fun getVideosList(query: String = "") {
        Observable.fromCallable {
            getVideos(query)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ videoList ->
                Log.d("gayathri_log", "videoList ${videoList.size}")
//                _videos.value = videoList
            }, {
                Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
            })
            .addTo(disposable)
    }

    private fun runInBackground(query: String = "") {
        Single.fromCallable {
            getVideos(query)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { videoList ->
                _videos.value = videoList
            }
            .addTo(disposable)
    }

    private fun findVideos(): MutableList<String> {
        val paths = mutableListOf<String>()
        val path = "/storage/emulated/0/yourfoldername"
        val directory = File(Environment.getExternalStorageDirectory().toString())
        Log.d("gayathri_log", "directory $directory")
        val files = directory.listFiles()
        Log.d("gayathri_log", "files $files")
        files?.forEach { file ->
            Log.d("gayathri_log", "list $file file.absolutePath ${file.isFile} ${file.isDirectory}")
            if (file.absolutePath.contains(".mp4")) {
                paths.add(file.absolutePath)
            } else if (file.isDirectory) {
                loadAllTheDirectoryFiles(file)
            }
        }
        return paths
    }

    private fun loadAllTheDirectoryFiles(file: File) {
        val files = file.listFiles()
        if (file.absolutePath.contains(".mp4")) {
            Log.d("gayathri_log", "$files ${file.absolutePath}")
        } else if (file.isDirectory) {
            loadAllTheDirectoryFiles(file)
        }
    }

    private fun getVideos(query: String): MutableList<VideoModel> {
        val videoList = mutableListOf<VideoModel>()
        val contentResolver = context.contentResolver
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.let {
            if (!it.isClosed) {
                while (it.moveToNext()) {
                    val title =
                        cursor.getString(it.getColumnIndex(MediaStore.Video.Media.TITLE))
                    val description =
                        cursor.getString(it.getColumnIndex(MediaStore.Video.Media.DESCRIPTION))
                    val displayName =
                        cursor.getString(it.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))
                    val album =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            cursor.getString(it.getColumnIndex(MediaStore.Video.Media.ALBUM))
                        } else ""
                    val createdDate =
                        cursor.getLong(it.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))

                    val videoUrl = Uri.fromFile(
                        File(
                            cursor.getString(
                                cursor.getColumnIndex(
                                    MediaStore.Video.Media.DATA
                                )
                            )
                        )
                    ).toString()
                    val videoModel =
                        VideoModel(
                            title = title,
                            description = description ?: "",
                            displayName = displayName,
                            album = album,
                            thumnail = getThumbnailBitmap(contentResolver, cursor, uri),
                            createdDate = createdDate,
                            videoUrl = videoUrl,
                            id = it.position
                        )
                    videoList.add(videoModel)
                    _video.postValue(videoModel)
                    _videos.postValue(videoList)
                }
                cursor.close()
            }
        }
        return videoList
    }

    private fun getThumbnailBitmap(
        contentResolver: ContentResolver,
        cursor: Cursor,
        uri: Uri
    ): Bitmap? {
        val option = BitmapFactory.Options()
        option.inSampleSize = 1
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver.loadThumbnail(uri, Size(50, 50), null)
        } else {
            val picturePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            ThumbnailUtils.createVideoThumbnail(
                picturePath,
                MediaStore.Video.Thumbnails.MICRO_KIND
            )
        }
    }

    fun setOrientation(orientation: Int) {
        when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> _spanCount.value = 3
            Configuration.ORIENTATION_PORTRAIT -> _spanCount.value = 2
        }
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

    fun setOnTextChange(query: CharSequence?) {
        query?.let {
            _searchResults.value = videosList.value?.filter {
                query.toString().toLowerCase(Locale.getDefault()).contains(
                    it.title
                        .toLowerCase(Locale.getDefault())
                )
            }
        }
    }
}