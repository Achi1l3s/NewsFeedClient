package com.faist.vknewsclient.domain.repository

import com.faist.vknewsclient.domain.entity.AuthState
import com.faist.vknewsclient.domain.entity.FeedPost
import com.faist.vknewsclient.domain.entity.PostComment
import kotlinx.coroutines.flow.StateFlow

interface NewsFeedRepository {

    fun getAuthStateFlow(): StateFlow<AuthState>

    fun getNewsFeed(): StateFlow<List<FeedPost>>

    fun getComments(feedPost: FeedPost): StateFlow<List<PostComment>>

    suspend fun changeLikeStatus(feedPost: FeedPost)

    suspend fun deleteFeedPost(feedPost: FeedPost)

    suspend fun loadNextData()

    suspend fun checkAuthState()
}