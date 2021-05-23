package com.gayathri.videogallery.viewmodel

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gayathri.videogallery.LocalStorageVideos.getLocalStorageVideos
import com.gayathri.videogallery.model.VideoModel
import com.gayathri.videogallery.model.VideoModel.Companion.create
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.*

class MainViewModel(val context: Context) : ViewModel() {

    val video: LiveData<VideoModel> get() = _video
    val spanCount: LiveData<Int> get() = _spanCount
    val videosList: LiveData<List<VideoModel>> get() = _videos
    val searchResults: LiveData<List<VideoModel>> get() = _searchResults

    private val _video: MutableLiveData<VideoModel> = MutableLiveData()
    private val _spanCount: MutableLiveData<Int> = MutableLiveData()
    private val _videos: MutableLiveData<List<VideoModel>> = MutableLiveData()
    private val _searchResults: MutableLiveData<List<VideoModel>> = MutableLiveData()

    var disposable: CompositeDisposable = CompositeDisposable()

    fun getVideosList() {
        Observable.fromCallable {
            getVideos()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ videoList ->
                _videos.value = videoList
            }, {
                Log.e("error", "$it")
            })
            .addTo(disposable)
    }

    private fun getVideos(): MutableList<VideoModel> {
        val videoList = mutableListOf<VideoModel>()
        val contentResolver = context.contentResolver

        val cursor = getLocalStorageVideos(contentResolver)
        cursor?.let {
            if (!it.isClosed) {
                while (it.moveToNext()) {
                    val videoModel = create(cursor, contentResolver)
                    videoList.add(videoModel)
                    _video.postValue(videoModel)
                    _videos.postValue(videoList)
                    Log.d("gayathri_log", "${videoList.size}")
                }
                cursor.close()
            }
        }
        return videoList
    }


    fun setOrientation(orientation: Int) {
        when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> _spanCount.value = SPAN_LANDSCAPE
            Configuration.ORIENTATION_PORTRAIT -> _spanCount.value = SPAN_PORTRAIT
        }
    }

    fun setOnTextChange(query: CharSequence?) {
        query?.let {
            val list = videosList.value?.filter {
                isSearchedVideoFound(query.toString(), it.title)
            }
            _searchResults.value = list
        }
    }

    private fun isSearchedVideoFound(query: String, title: String): Boolean =
        title.toLowerCase(Locale.getDefault()).contains(
            query.toLowerCase(Locale.getDefault())
        )

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

    companion object {
        private const val SPAN_PORTRAIT = 2
        private const val SPAN_LANDSCAPE = 3
    }
}
