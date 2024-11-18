package com.faist.vknewsclient.data.repository

import android.app.Application
import android.util.Log
import com.faist.vknewsclient.data.mapper.NewsFeedMapper
import com.faist.vknewsclient.data.network.ApiFactory
import com.faist.vknewsclient.domain.FeedPost
import com.faist.vknewsclient.domain.PostComment
import com.faist.vknewsclient.domain.StatisticItem
import com.faist.vknewsclient.domain.StatisticType
import com.vk.api.sdk.VKPreferencesKeyValueStorage
import com.vk.api.sdk.auth.VKAccessToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class NewsFeedRepository(application: Application) {

    private val storage = VKPreferencesKeyValueStorage(application)
    private val token = VKAccessToken.restore(storage)

    private val coroutineScope = CoroutineScope(context = Dispatchers.Default)
    private val nextDataNeededEvents = MutableSharedFlow<Unit>(replay = 1)

    private val apiService = ApiFactory.apiService
    private val mapper = NewsFeedMapper()

    private var _feedPosts = mutableListOf<FeedPost>()
    val feedPosts: List<FeedPost>
        get() = _feedPosts.toList()

    private var _postComments = mutableListOf<PostComment>()
    val postComments: List<PostComment>
        get() = _postComments

    private var nextFrom: String? = null
    private var nextCommentsFrom: Int? = null

    val newsFeed: StateFlow<List<FeedPost>> = flow {
        nextDataNeededEvents.emit(Unit)
        nextDataNeededEvents.collect {
            val startFrom = nextFrom
            if (nextFrom == null && feedPosts.isNotEmpty()) {
                emit(feedPosts)
                return@collect
            }

            val response =
                if (startFrom == null) {
                    apiService.loadNewsFeed(getAccessToken())
                } else {
                    apiService.loadNewsFeed(getAccessToken(), startFrom)
                }

            nextFrom = response.newsFeedContent.nextFrom
            val posts = mapper.mapResponseToPosts(response)

            _feedPosts.addAll(posts)
            emit(feedPosts)
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = feedPosts
    )

    suspend fun loadNextData() {
        nextDataNeededEvents.emit(Unit)
    }

    private fun getAccessToken(): String {
        return token?.accessToken ?: throw IllegalStateException("access token is null")
    }

    suspend fun changeLikeStatus(feedPost: FeedPost) {
        val response =
            if (feedPost.isLiked) {
                apiService.deleteLike(
                    token = getAccessToken(),
                    ownerId = feedPost.communityId,
                    postId = feedPost.id
                )
            } else {
                apiService.addLike(
                    token = getAccessToken(),
                    ownerId = feedPost.communityId,
                    postId = feedPost.id
                )
            }
        val newLikesCount = response.likes.count

        val newStatistics = feedPost.statistics
            .toMutableList()
            .apply {
                removeIf { it.type == StatisticType.LIKES }
                add(StatisticItem(type = StatisticType.LIKES, newLikesCount))
            }

        val newFeedPost = feedPost.copy(statistics = newStatistics, isLiked = !feedPost.isLiked)
        val postIndex = _feedPosts.indexOf(feedPost)
        _feedPosts[postIndex] = newFeedPost
    }

    suspend fun deleteFeedPost(feedPost: FeedPost) {
        apiService.ignorePost(
            token = getAccessToken(),
            ownerId = feedPost.communityId,
            postId = feedPost.id
        )
        _feedPosts.remove(feedPost)
    }

    suspend fun loadPostComments(feedPost: FeedPost): List<PostComment> {
        val startFrom = nextCommentsFrom
        Log.d("MediaCheck", "NewsFeedRepository, 'loadPostComments' BLOCK, startFrom: $startFrom")
        if (nextCommentsFrom == null && postComments.isNotEmpty()) return postComments

        val response =
            if (startFrom == null) {
                apiService.loadComments(
                    token = getAccessToken(),
                    ownerId = feedPost.communityId,
                    postId = feedPost.id,
                    count = 20
                )
            } else {
                apiService.loadComments(
                    token = getAccessToken(),
                    ownerId = feedPost.communityId,
                    postId = feedPost.id,
                    startCommentsFrom = startFrom,
                    count = 20 + startFrom
                )
            }
        nextCommentsFrom = response.commentsContent.startCommentsFrom

        _postComments.addAll(mapper.mapResponseToComments(response))
        Log.d("MediaCheck", "Repository, post_id: ${feedPost.id} and ownerId: ${feedPost.communityId}")
        Log.d("MediaCheck", "Repository, 'loadPostComments' BLOCK, response: ${response.commentsContent.comments.first()}")
        return postComments
    }
}