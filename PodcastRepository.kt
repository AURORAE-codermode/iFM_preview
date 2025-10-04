package com.zjgsu.ifm_preview.data.repository

import com.zjgsu.ifm_preview.data.model.Episode
import com.zjgsu.ifm_preview.data.model.Podcast
import kotlinx.coroutines.delay

class PodcastRepository {
    
    private val mockPodcasts = listOf(
        Podcast(
            id = "1",
            title = "科技前沿",
            description = "最新科技动态和趋势分析，涵盖人工智能、区块链、物联网等前沿技术",
            coverImage = "https://picsum.photos/300/300?random=1",
            author = "科技之声",
            category = "科技",
            episodes = listOf(
                Episode(
                    id = "101",
                    podcastId = "1",
                    title = "AI技术的最新突破",
                    description = "深度探讨人工智能在自然语言处理和计算机视觉领域的最新进展",
                    audioUrl = "https://www.soundjay.com/button/button-1.mp3",
                    duration = "45:30",
                    publishDate = "2024-01-15"
                ),
                Episode(
                    id = "102",
                    podcastId = "1",
                    title = "区块链与数字货币",
                    description = "分析区块链技术的发展现状和数字货币的未来趋势",
                    audioUrl = "https://www.soundjay.com/button/button-2.mp3",
                    duration = "38:15",
                    publishDate = "2024-01-08"
                )
            ),
            subscriptionCount = 12500
        ),
        Podcast(
            id = "2",
            title = "历史故事",
            description = "讲述有趣的历史事件和人物故事，让历史变得生动有趣",
            coverImage = "https://picsum.photos/300/300?random=2",
            author = "历史频道",
            category = "历史",
            episodes = listOf(
                Episode(
                    id = "201",
                    podcastId = "2",
                    title = "秦始皇统一六国",
                    description = "详细讲述秦始皇如何完成中国历史上第一次大一统",
                    audioUrl = "https://www.soundjay.com/button/button-3.mp3",
                    duration = "52:10",
                    publishDate = "2024-01-10"
                )
            ),
            subscriptionCount = 8900
        ),
        Podcast(
            id = "3",
            title = "商业思维",
            description = "分享商业智慧和创业经验，帮助你在商场上取得成功",
            coverImage = "https://picsum.photos/300/300?random=3",
            author = "商业洞察",
            category = "商业",
            episodes = listOf(
                Episode(
                    id = "301",
                    podcastId = "3",
                    title = "初创企业的融资策略",
                    description = "为创业者提供实用的融资建议和技巧",
                    audioUrl = "https://www.soundjay.com/button/button-4.mp3",
                    duration = "41:25",
                    publishDate = "2024-01-12"
                )
            ),
            subscriptionCount = 15600
        )
    )
    
    suspend fun getPodcasts(): List<Podcast> {
        delay(1000)
        return mockPodcasts
    }
    
    suspend fun searchPodcasts(query: String): List<Podcast> {
        delay(500)
        return if (query.isBlank()) {
            mockPodcasts
        } else {
            mockPodcasts.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.author.contains(query, ignoreCase = true)
            }
        }
    }
    
    suspend fun getPodcastById(id: String): Podcast? {
        delay(300)
        return mockPodcasts.find { it.id == id }
    }
    
    suspend fun toggleSubscription(podcastId: String, subscribe: Boolean) {
        delay(200)
    }
}
