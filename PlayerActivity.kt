package com.zjgsu.ifm_preview.presentation.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.zjgsu.ifm_preview.data.model.Episode
import com.zjgsu.ifm_preview.data.model.Podcast
import com.zjgsu.ifm_preview.databinding.ActivityPlayerBinding
import com.zjgsu.ifm_preview.presentation.adapter.EpisodeAdapter
import com.zjgsu.ifm_preview.service.PodcastPlayerService

class PlayerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPlayerBinding
    private var podcast: Podcast? = null
    private var currentEpisode: Episode? = null
    
    private var playerService: PodcastPlayerService? = null
    private var isServiceBound = false
    
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            val binder = service as PodcastPlayerService.PodcastPlayerBinder
            playerService = binder.getService()
            isServiceBound = true
            updatePlayerUI()
        }
        
        override fun onServiceDisconnected(arg0: ComponentName?) {
            isServiceBound = false
            playerService = null
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        podcast = intent.getParcelableExtra("podcast")
        currentEpisode = intent.getParcelableExtra("episode")
        
        initializeViews()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }
    
    private fun initializeViews() {
        setupToolbar()
        updatePodcastInfo()
        setupPlayerControls()
        setupEpisodeList()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    
    private fun updatePodcastInfo() {
        podcast?.let { podcast ->
            binding.textViewPodcastTitle.text = podcast.title
            binding.textViewPodcastAuthor.text = podcast.author
            binding.textViewPodcastDescription.text = podcast.description
            
            com.bumptech.glide.Glide.with(this)
                .load(podcast.coverImage)
                .placeholder(android.R.drawable.ic_media_play)
                .into(binding.imageViewCover)
                
            updateSubscribeButton(podcast.isSubscribed)
        }
        
        currentEpisode?.let { episode ->
            binding.textViewEpisodeTitle.text = episode.title
            binding.textViewEpisodeDescription.text = episode.description
            binding.textViewEpisodeDuration.text = episode.getFormattedDuration()
        }
    }
    
    private fun setupPlayerControls() {
        binding.buttonPlayPause.setOnClickListener {
            togglePlayPause()
        }
        
        binding.buttonPrevious.setOnClickListener {
            playPreviousEpisode()
        }
        
        binding.buttonNext.setOnClickListener {
            playNextEpisode()
        }
        
        binding.buttonSubscribe.setOnClickListener {
            toggleSubscription()
        }
    }
    
    private fun setupEpisodeList() {
        podcast?.episodes?.let { episodes ->
            val adapter = EpisodeAdapter(episodes) { episode ->
                playEpisode(episode)
            }
            binding.recyclerViewEpisodes.adapter = adapter
            binding.recyclerViewEpisodes.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        }
    }
    
    private fun bindPlayerService() {
        val intent = Intent(this, PodcastPlayerService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
    
    private fun togglePlayPause() {
        if (isServiceBound) {
            playerService?.togglePlayPause()
            updatePlayPauseButton()
        } else if (currentEpisode != null) {
            startPlayback()
        }
    }
    
    private fun startPlayback() {
        currentEpisode?.let { episode ->
            val intent = Intent(this, PodcastPlayerService::class.java).apply {
                action = PodcastPlayerService.ACTION_PLAY
                putExtra("episode", episode)
            }
            ContextCompat.startForegroundService(this, intent)
            bindPlayerService()
        }
    }
    
    private fun playPreviousEpisode() {
        Toast.makeText(this, "播放上一集", Toast.LENGTH_SHORT).show()
    }
    
    private fun playNextEpisode() {
        Toast.makeText(this, "播放下一集", Toast.LENGTH_SHORT).show()
    }
    
    private fun toggleSubscription() {
        podcast?.let { podcast ->
            val newSubscribeState = !podcast.isSubscribed
            updateSubscribeButton(newSubscribeState)
            Toast.makeText(this, if (newSubscribeState) "已订阅" else "已取消订阅", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun updateSubscribeButton(isSubscribed: Boolean) {
        if (isSubscribed) {
            binding.buttonSubscribe.text = "已订阅"
            binding.buttonSubscribe.setBackgroundColor(
                ContextCompat.getColor(this, android.R.color.darker_gray)
            )
        } else {
            binding.buttonSubscribe.text = "订阅"
            binding.buttonSubscribe.setBackgroundColor(
                ContextCompat.getColor(this, android.R.color.holo_blue_dark)
            )
        }
    }
    
    private fun playEpisode(episode: Episode) {
        currentEpisode = episode
        updatePodcastInfo()
        startPlayback()
    }
    
    private fun updatePlayerUI() {
        updatePlayPauseButton()
    }
    
    private fun updatePlayPauseButton() {
        val isPlaying = playerService?.isPlaying() ?: false
        if (isPlaying) {
            binding.buttonPlayPause.setImageResource(android.R.drawable.ic_media_pause)
        } else {
            binding.buttonPlayPause.setImageResource(android.R.drawable.ic_media_play)
        }
    }
}
