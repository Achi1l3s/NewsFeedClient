package com.faist.vknewsclient.data.repository

import android.util.Log
import com.faist.vknewsclient.data.mapper.NewsFeedMapper
import com.faist.vknewsclient.data.network.ApiService
import com.faist.vknewsclient.domain.entity.*
import com.faist.vknewsclient.domain.repository.NewsFeedRepository
import com.faist.vknewsclient.extensions.mergeWith
import com.vk.api.sdk.VKPreferencesKeyValueStorage
import com.vk.api.sdk.auth.VKAccessToken
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class NewsFeedRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val mapper: NewsFeedMapper,
    private val storage: VKPreferencesKeyValueStorage
) : NewsFeedRepository {

    private val token
        get() = VKAccessToken.restore(storage)

    private val coroutineScope = CoroutineScope(context = Dispatchers.Default)
    private val nextDataNeededEvents = MutableSharedFlow<Unit>(replay = 1)
    private val refreshedListFlow = MutableSharedFlow<List<FeedPost>>()

    private var _feedPosts = mutableListOf<FeedPost>()
    private val feedPosts: List<FeedPost>
        get() = _feedPosts.toList()

    private var nextFrom: String? = null
//    private var nextCommentsFrom: Int? = null

    private val checkAuthStateEvents = MutableSharedFlow<Unit>(replay = 1)

    private val authStateFlow = flow {
        checkAuthStateEvents.emit(Unit)
        checkAuthStateEvents.collect {
            val currentToken = token
            val loggedIn = currentToken != null && currentToken.isValid
            val authState = if (loggedIn) AuthState.Authorized else AuthState.NotAuthorized
            Log.d("MYToken", "Access token: ${token?.accessToken}")
            emit(authState)
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = AuthState.Initial
    )

    private val loadedListFlow = flow {
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
    }
        .retry(2) {
            delay(RETRY_TIMEOUT_MILLIS)
            true
        }

    override suspend fun checkAuthState() {
        checkAuthStateEvents.emit(Unit)
    }

    override fun getAuthStateFlow(): StateFlow<AuthState> {
        return authStateFlow
    }

    override fun getNewsFeed(): StateFlow<List<FeedPost>> {
        return newsFeed
    }

    override fun getComments(feedPost: FeedPost): StateFlow<List<PostComment>> = flow {
        val response = apiService.loadComments(
                token = getAccessToken(),
                ownerId = feedPost.communityId,
                postId = feedPost.id
            )
        emit(mapper.mapResponseToComments(response))
    }.retry {
        delay(RETRY_TIMEOUT_MILLIS)
        true

    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = listOf()
    )

    private val newsFeed: StateFlow<List<FeedPost>> = loadedListFlow
        .mergeWith(refreshedListFlow)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Lazily,
            initialValue = feedPosts
        )

    override suspend fun loadNextData() {
        nextDataNeededEvents.emit(Unit)
    }

    private fun getAccessToken(): String {
        return token?.accessToken ?: throw IllegalStateException("access token is null")
    }

    override suspend fun changeLikeStatus(feedPost: FeedPost) {
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
        refreshedListFlow.emit(feedPosts)
    }

    override suspend fun deleteFeedPost(feedPost: FeedPost) {
        apiService.ignorePost(
            token = getAccessToken(),
            ownerId = feedPost.communityId,
            postId = feedPost.id
        )
        _feedPosts.remove(feedPost)
        refreshedListFlow.emit(feedPosts)
    }

    companion object {

        private const val RETRY_TIMEOUT_MILLIS = 3000L
    }
}