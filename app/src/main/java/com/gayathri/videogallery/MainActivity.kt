package com.gayathri.videogallery

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.gayathri.evaluationsample.presentation.adapter.PopularNewsAdapter
import com.gayathri.videogallery.viewmodel.MainViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val viewModel by inject<MainViewModel>()
    private lateinit var videoAdapter: PopularNewsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
    }

    private fun initUI() {
        initRecyclerView()
        observeLiveData()
        handleSearchClick()
        setSearchTypeListener()
    }

    private fun setSearchTypeListener() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setOnTextChange(s)
                if (s?.length == 1) {
                    observeSearchResultsLiveData()
                } else if (s?.length == 0) {
                    viewModel.searchResults.removeObservers(this@MainActivity)
                }
            }
        })
    }

    private fun handleSearchClick() {
        ivSearch.setOnClickListener {
            ivSearch.isSelected = !ivSearch.isSelected
            handleSearchbarVisiblity()

            if (!ivSearch.isSelected) {
                etSearch.text?.clear()
            }
        }
    }

    private fun handleSearchbarVisiblity() {
        etSearch.visibility = if (ivSearch.isSelected) View.VISIBLE else View.GONE
    }

    private fun initRecyclerView() {
        //Create Adapter and set up recycler view with adapter
        videoAdapter = PopularNewsAdapter(itemClickLister)
        viewModel.setOrientation(resources.configuration.orientation)
        recyclerView?.adapter = videoAdapter
    }

    private val itemClickLister = object : ItemClickListener {
        override fun onItemClick(item: VideoModel, position: Int) {
            val intent = Intent(this@MainActivity, PlayerActivity::class.java)
            intent.putExtra("url", item.videoUrl)
            intent.putExtra("position", position)
            startActivity(intent)
        }
    }

    private fun setLayoutManager(spanCount: Int) {
        recyclerView?.layoutManager = createLayoutManager(this, spanCount)
//        val spanCount = 3 // 3 columns
//        val spacing = 50 // 50px
//        val includeEdge = true
//        recyclerView.addItemDecoration(
//            GridSpacingItemDecoration(
//                this,
//                spanCount,
//                spacing,
//                includeEdge
//            )
//        )
    }

    private fun createLayoutManager(
        context: Context?,
        spanCount: Int
    ) = GridLayoutManager(context, spanCount)

    private fun observeLiveData() {
        viewModel.getVideosList()
        observeVideosLiveData()
        observeSpanCountLiveData()
    }

    private fun observeSearchResultsLiveData() {
        viewModel.searchResults.observe(this, Observer { videos ->
            videoAdapter.itemList = videos
        })
    }

    private fun observeVideosLiveData() {
        val videos = mutableListOf<VideoModel>()
        viewModel.video.observe(this, Observer { item ->
            videos.add(item)
            if (!viewModel.searchResults.hasActiveObservers()) {
                videoAdapter.itemList = videos
            }
        })
    }

    private fun observeSpanCountLiveData() {
        viewModel.spanCount.observe(this, Observer { count ->
            setLayoutManager(count)
        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.setOrientation(newConfig.orientation)
    }

}

interface ItemClickListener {
    fun onItemClick(item: VideoModel, position: Int)
}