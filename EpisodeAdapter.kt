package com.zjgsu.ifm_preview.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zjgsu.ifm_preview.data.model.Episode
import com.zjgsu.ifm_preview.databinding.ItemEpisodeBinding

class EpisodeAdapter(
    private val episodes: List<Episode>,
    private val onItemClickListener: (Episode) -> Unit
) : ListAdapter<Episode, EpisodeAdapter.EpisodeViewHolder>(EpisodeDiffCallback()) {

    inner class EpisodeViewHolder(
        private val binding: ItemEpisodeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        private var currentEpisode: Episode? = null
        
        init {
            binding.root.setOnClickListener {
                currentEpisode?.let { episode ->
                    onItemClickListener(episode)
                }
            }
        }
        
        fun bind(episode: Episode) {
            currentEpisode = episode
            
            binding.textViewTitle.text = episode.title
            binding.textViewDescription.text = episode.description
            binding.textViewDuration.text = episode.getFormattedDuration()
            binding.textViewPublishDate.text = episode.publishDate
            
            if (episode.isPlayed) {
                binding.imageViewPlayed.visibility = android.view.View.VISIBLE
            } else {
                binding.imageViewPlayed.visibility = android.view.View.GONE
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val binding = ItemEpisodeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EpisodeViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = getItem(position)
        holder.bind(episode)
    }
    
    override fun getItemCount(): Int = episodes.size
    
    override fun getItem(position: Int): Episode = episodes[position]
}

class EpisodeDiffCallback : DiffUtil.ItemCallback<Episode>() {
    override fun areItemsTheSame(oldItem: Episode, newItem: Episode): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: Episode, newItem: Episode): Boolean {
        return oldItem == newItem
    }
}
