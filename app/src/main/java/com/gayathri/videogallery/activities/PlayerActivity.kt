package com.gayathri.videogallery.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.gayathri.videogallery.R
import com.gayathri.videogallery.viewmodel.MainViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_player.*
import org.koin.android.ext.android.inject

class PlayerActivity : AppCompatActivity() {
    private val viewModel by inject<MainViewModel>()
    private lateinit var player: Player

    private val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(this, "exoplayer-sample")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        initUI()
    }

    private fun initUI() {
        viewModel.getVideosList()
        viewModel.video.observe(this, Observer { item ->
            val mediaItem =
                MediaItem.Builder()
                    .setMediaId(item.id.toString())
                    .setUri(item.videoUrl ?: "")
                    .setTag(item.title)
                    .build()
            player.addMediaItem(mediaItem)

        })

        setMuteListener()
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    private fun setMuteListener() {
        exo_extra_controls.setOnClickListener {
            exo_extra_controls.isSelected = !player.isDeviceMuted
            player.isDeviceMuted = !player.isDeviceMuted
        }
    }

    private fun initializePlayer() {
        player = SimpleExoPlayer.Builder(this).build()
        preparePlayer()
        playerView.player = player
        player.playWhenReady = true
        player.play()
    }


    private fun preparePlayer() {
        val mediaSource = buildMediaSource()
        (player as SimpleExoPlayer).setMediaSource(mediaSource)
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun releasePlayer() {
        player.release()
    }

    private fun buildMediaSource(type: String = ""): MediaSource {
        val mediaItem: MediaItem =
            MediaItem.fromUri(intent.getStringExtra("url") ?: "")
        return if (type == "dash") {
            DashMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)
        } else {
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        player.let {
            if (it.isPlaying) {
                it.stop()
            }
        }

    }
}
