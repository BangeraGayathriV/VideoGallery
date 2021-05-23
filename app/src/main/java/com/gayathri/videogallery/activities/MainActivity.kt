package com.gayathri.videogallery.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.gayathri.videogallery.GridSpacingItemDecoration
import com.gayathri.videogallery.R
import com.gayathri.videogallery.model.VideoModel
import com.gayathri.videogallery.`interface`.ItemClickListener
import com.gayathri.videogallery.adapter.PopularNewsAdapter
import com.gayathri.videogallery.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val viewModel by inject<MainViewModel>()
    private lateinit var videoAdapter: PopularNewsAdapter
    private var backPressCount: Int = 0
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
        recyclerView?.addItemDecoration(
            GridSpacingItemDecoration(
                this
            )
        )
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
    }

    private fun createLayoutManager(
        context: Context?,
        spanCount: Int
    ) = GridLayoutManager(context, spanCount)

    private fun observeLiveData() {
        getLiveData()
        observeVideosLiveData()
        observeSpanCountLiveData()
    }

    private fun getLiveData() {
        viewModel.getVideosList()
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

    override fun onBackPressed() {
        when {
            ivSearch.isSelected -> clearSearch()
            backPressCount == 1 -> super.onBackPressed()
            else -> {
                backPressCount++
                Toast.makeText(this, "Back press again to exit the app", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearSearch() {
        etSearch.text?.clear()
        ivSearch.isSelected = false
        handleSearchbarVisiblity()
        backPressCount++
    }

    override fun onResume() {
        super.onResume()
        checkAppHasPermissionToReadStorage()
    }

    private fun checkAppHasPermissionToReadStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                0
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        getLiveData()
    }

    override fun onStart() {
        super.onStart()
        checkAppHasPermissionToReadStorage()
    }

    override fun onDestroy() {
        super.onDestroy()
        removedVideoObserver()
        removedSearchResultObserver()
    }

    private fun removedSearchResultObserver() {
        if (viewModel.searchResults.hasActiveObservers()) {
            viewModel.searchResults.removeObservers(this)
        }
    }

    private fun removedVideoObserver() {
        if (viewModel.video.hasActiveObservers()) {
            viewModel.video.removeObservers(this)
        }
    }

}
