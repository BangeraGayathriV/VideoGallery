package com.gayathri.videogallery

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.gayathri.videogallery.viewmodel.MainViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.drm.DrmSessionManager
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.Extractor
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy
import kotlinx.android.synthetic.main.activity_player.*
import kotlinx.android.synthetic.main.custom_exo_player_view.*
import org.koin.android.ext.android.inject


class PlayerActivity : AppCompatActivity() {
    private val viewModel by inject<MainViewModel>()
    private var videoUrls: MutableList<MediaItem> = mutableListOf()
    private lateinit var player: Player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        initUI()
    }

    private fun initUI() {
        initializePlayer()
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

        exo_play.setOnClickListener {
            Toast.makeText(this, "state exo_play ${player.currentPosition}", Toast.LENGTH_SHORT)
                .show()
            player.prepare()
            player.play()
//            player.playWhenReady = true
            val dataSource = DefaultDataSourceFactory(this)
            val extractor = DefaultExtractorsFactory()
//            val mediaSource = ProgressiveMediaSource(
//                player.getMediaItemAt(intent.getIntExtra("position", 0)),
//                dataSource,extractor,DrmSessionManager.DRM_UNSUPPORTED,LoadErrorHandlingPolicy.LoadErrorInfo(),9)
//            )

            player.getMediaItemAt(intent.getIntExtra("position", 0))
        }

        setMuteListener()
    }

    private fun setMuteListener() {
        exo_extra_controls.setOnClickListener {
            exo_extra_controls.isSelected = !player.isDeviceMuted
            player.isDeviceMuted = !player.isDeviceMuted
        }
    }

    private fun initializePlayer() {
        player = SimpleExoPlayer.Builder(this).build()
        playerView.setPlayer(player)
        val mediaItem: MediaItem =
            MediaItem.fromUri(intent.getStringExtra("url") ?: "")
//            MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3")
//        player.addMediaItem(intent.getIntExtra("position", 0), mediaItem)
//        player.moveMediaItem(0,intent.getIntExtra("position", 0))
        Log.d("gayathri_log", "${intent.getIntExtra("position", 0)}")
//        player?.playWhenReady = true
//        player?.play()

//        player.addListener(object : Player.Listener {
//            override fun onTracksChanged(
//                trackGroups: TrackGroupArray,
//                trackSelections: TrackSelectionArray
//            ) {
//                Toast.makeText(this@PlayerActivity, "state $trackGroups", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onPlaybackStateChanged(state: Int) {
//                Toast.makeText(this@PlayerActivity, "state $state", Toast.LENGTH_SHORT).show()
//
//            }
//        })
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.let {
            if (it.isPlaying) {
                it.stop()
            }
        }

    }
}