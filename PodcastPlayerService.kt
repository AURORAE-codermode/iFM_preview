package com.zjgsu.ifm_preview.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.zjgsu.ifm_preview.R
import com.zjgsu.ifm_preview.data.model.Episode
import com.zjgsu.ifm_preview.presentation.activity.PlayerActivity

class PodcastPlayerService : Service() {
    
    private var mediaPlayer: MediaPlayer? = null
    private var currentEpisode: Episode? = null
    private var isPrepared = false
    
    private val binder = PodcastPlayerBinder()
    
    inner class PodcastPlayerBinder : Binder() {
        fun getService(): PodcastPlayerService = this@PodcastPlayerService
    }
    
    override fun onBind(intent: Intent?): IBinder = binder
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                val episode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra("episode", Episode::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra("episode")
                }
                episode?.let { playEpisode(it) }
            }
            ACTION_PAUSE -> pause()
            ACTION_STOP -> stop()
        }
        
        return START_STICKY
    }
    
    fun playEpisode(episode: Episode) {
        currentEpisode = episode
        
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(episode.audioUrl)
            setOnPreparedListener {
                isPrepared = true
                start()
                updateNotification()
            }
            setOnCompletionListener {
                updateNotification()
            }
            prepareAsync()
        }
        
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
    }
    
    fun togglePlayPause() {
        if (isPrepared) {
            if (mediaPlayer?.isPlaying == true) {
                pause()
            } else {
                play()
            }
        }
    }
    
    fun play() {
        mediaPlayer?.start()
        updateNotification()
    }
    
    fun pause() {
        mediaPlayer?.pause()
        updateNotification()
    }
    
    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPrepared = false
        stopForeground(true)
        stopSelf()
    }
    
    fun seekTo(position: Int) {
        if (isPrepared) {
            mediaPlayer?.seekTo(position)
        }
    }
    
    fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false
    
    fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0
    
    fun getDuration(): Int = mediaPlayer?.duration ?: 0
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "播客播放",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "播客播放通知"
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun buildNotification(): Notification {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val playPauseAction = if (isPlaying()) {
            NotificationCompat.Action(
                android.R.drawable.ic_media_pause,
                "暂停",
                getPendingIntent(ACTION_PAUSE)
            )
        } else {
            NotificationCompat.Action(
                android.R.drawable.ic_media_play,
                "播放",
                getPendingIntent(ACTION_PLAY)
            )
        }
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(currentEpisode?.title ?: "播客")
            .setContentText(currentEpisode?.podcastId ?: "正在播放")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_media_previous,
                "上一集",
                getPendingIntent(ACTION_PREVIOUS)
            )
            .addAction(playPauseAction)
            .addAction(
                android.R.drawable.ic_media_next,
                "下一集",
                getPendingIntent(ACTION_NEXT)
            )
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
            .build()
    }
    
    private fun updateNotification() {
        val notification = buildNotification()
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    private fun getPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, PodcastPlayerService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
    
    companion object {
        const val ACTION_PLAY = "PLAY"
        const val ACTION_PAUSE = "PAUSE"
        const val ACTION_STOP = "STOP"
        const val ACTION_NEXT = "NEXT"
        const val ACTION_PREVIOUS = "PREVIOUS"
        
        private const val CHANNEL_ID = "podcast_player_channel"
        private const val NOTIFICATION_ID = 1
    }
}
