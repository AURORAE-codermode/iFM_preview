package com.zjgsu.ifm_preview.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.zjgsu.ifm_preview.data.model.Podcast
import com.zjgsu.ifm_preview.databinding.ActivityMainBinding
import com.zjgsu.ifm_preview.presentation.adapter.PodcastAdapter
import com.zjgsu.ifm_preview.presentation.viewmodel.PodcastViewModel

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: PodcastViewModel by viewModels()
    private lateinit var podcastAdapter: PodcastAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initializeViews()
        setupObservers()
    }
    
    private fun initializeViews() {
        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
        setupSearchView()
    }
    
    private fun setupObservers() {
        viewModel.podcasts.observe(this) { podcasts ->
            podcastAdapter.submitList(podcasts)
            binding.swipeRefresh.isRefreshing = false
            
            if (podcasts.isEmpty()) {
                binding.textEmpty.visibility = android.view.View.VISIBLE
                binding.recyclerView.visibility = android.view.View.GONE
            } else {
                binding.textEmpty.visibility = android.view.View.GONE
                binding.recyclerView.visibility = android.view.View.VISIBLE
            }
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        }
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "播客推荐"
    }
    
    private fun setupRecyclerView() {
        podcastAdapter = PodcastAdapter(
            onItemClickListener = { podcast ->
                navigateToPodcastDetail(podcast)
            },
            onSubscribeClickListener = { podcast, subscribe ->
                viewModel.toggleSubscription(podcast.id, subscribe)
                Toast.makeText(
                    this, 
                    if (subscribe) "订阅成功" else "已取消订阅", 
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = podcastAdapter
        }
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadPodcasts()
        }
    }
    
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchPodcasts(it) }
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.loadPodcasts()
                }
                return true
            }
        })
    }
    
    private fun navigateToPodcastDetail(podcast: Podcast) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra("podcast", podcast)
            if (podcast.episodes.isNotEmpty()) {
                putExtra("episode", podcast.episodes[0])
            }
        }
        startActivity(intent)
    }
}
