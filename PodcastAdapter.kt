package com.zjgsu.ifm_preview.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zjgsu.ifm_preview.data.model.Podcast
import com.zjgsu.ifm_preview.databinding.ItemPodcastBinding

class PodcastAdapter(
    private val onItemClickListener: (Podcast) -> Unit,
    private val onSubscribeClickListener: (Podcast, Boolean) -> Unit
) : ListAdapter<Podcast, PodcastAdapter.PodcastViewHolder>(PodcastDiffCallback()) {

    inner class PodcastViewHolder(
        private val binding: ItemPodcastBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        private var currentPodcast: Podcast? = null
        
        init {
            binding.root.setOnClickListener {
                currentPodcast?.let { podcast ->
                    onItemClickListener(podcast)
                }
            }
            
            binding.buttonSubscribe.setOnClickListener {
                currentPodcast?.let { podcast ->
                    val newSubscribeState = !podcast.isSubscribed
                    onSubscribeClickListener(podcast, newSubscribeState)
                }
            }
        }
        
        fun bind(podcast: Podcast) {
            currentPodcast = podcast
            
            binding.textViewTitle.text = podcast.title
            binding.textViewAuthor.text = podcast.author
            binding.textViewDescription.text = podcast.description
            binding.textViewSubscribers.text = formatSubscriberCount(podcast.subscriptionCount)
            
            updateSubscribeButton(podcast.isSubscribed)
            loadCoverImage(podcast.coverImage)
        }
        
        private fun updateSubscribeButton(isSubscribed: Boolean) {
            if (isSubscribed) {
                binding.buttonSubscribe.text = "已订阅"
                binding.buttonSubscribe.setBackgroundColor(
                    ContextCompat.getColor(binding.root.context, android.R.color.darker_gray)
                )
            } else {
                binding.buttonSubscribe.text = "订阅"
                binding.buttonSubscribe.setBackgroundColor(
                    ContextCompat.getColor(binding.root.context, android.R.color.holo_blue_dark)
                )
            }
        }
        
        private fun loadCoverImage(imageUrl: String) {
            Glide.with(binding.imageViewCover.context)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_media_play)
                .error(android.R.drawable.ic_media_play)
                .centerCrop()
                .into(binding.imageViewCover)
        }
        
        private fun formatSubscriberCount(count: Int): String {
            return when {
                count >= 1_000_000 -> "${count / 1_000_000}M"
                count >= 1_000 -> "${count / 1_000}K"
                else -> count.toString()
            } + " 订阅"
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastViewHolder {
        val binding = ItemPodcastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PodcastViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: PodcastViewHolder, position: Int) {
        val podcast = getItem(position)
        holder.bind(podcast)
    }
}

class PodcastDiffCallback : DiffUtil.ItemCallback<Podcast>() {
    override fun areItemsTheSame(oldItem: Podcast, newItem: Podcast): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: Podcast, newItem: Podcast): Boolean {
        return oldItem == newItem
    }
}
