package com.zjgsu.ifm_preview.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zjgsu.ifm_preview.data.model.Podcast
import com.zjgsu.ifm_preview.data.repository.PodcastRepository
import kotlinx.coroutines.launch

class PodcastViewModel : ViewModel() {
    
    private val repository = PodcastRepository()
    
    private val _podcasts = MutableLiveData<List<Podcast>>()
    val podcasts: LiveData<List<Podcast>> = _podcasts
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    init {
        loadPodcasts()
    }
    
    fun loadPodcasts() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val podcastsList = repository.getPodcasts()
                _podcasts.value = podcastsList
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun searchPodcasts(query: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val searchResults = repository.searchPodcasts(query)
                _podcasts.value = searchResults
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun toggleSubscription(podcastId: String, subscribe: Boolean) {
        viewModelScope.launch {
            try {
                repository.toggleSubscription(podcastId, subscribe)
                val currentList = _podcasts.value ?: emptyList()
                val updatedList = currentList.map { podcast ->
                    if (podcast.id == podcastId) {
                        podcast.copy(isSubscribed = subscribe)
                    } else {
                        podcast
                    }
                }
                _podcasts.value = updatedList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
