package com.faist.vknewsclient.presentation.news

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.faist.vknewsclient.data.repository.NewsFeedRepository
import com.faist.vknewsclient.domain.FeedPost
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class NewsFeedViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NewsFeedRepository(application)

    private val newsFeedFlow = repository.newsFeed

    private val loadNextDataEvents = MutableSharedFlow<Unit>()
    private val loadNextDataFlow = flow {
        loadNextDataEvents.collect {
            emit(
                NewsFeedScreenState.Posts(
                    posts = newsFeedFlow.value,
                    nextDataIsLoading = true
                )
            )
        }
    }

    val screenState = newsFeedFlow
        .filter { it.isNotEmpty() }
        .map { NewsFeedScreenState.Posts(posts = it) as NewsFeedScreenState }
        .onStart { emit(NewsFeedScreenState.Loading) }

    fun loadNextNewsFeed() {
        viewModelScope.launch {
            loadNextDataEvents.emit(Unit)
        }
    }

    fun changeLikeStatus(feedPost: FeedPost) {
        viewModelScope.launch {
            repository.changeLikeStatus(feedPost)
            _screenState.value = NewsFeedScreenState.Posts(posts = repository.feedPosts)
        }
    }

    fun removeFeedPost(feedPost: FeedPost) {
        viewModelScope.launch {
            repository.deleteFeedPost(feedPost)
            _screenState.value = NewsFeedScreenState.Posts(posts = repository.feedPosts)
        }
    }
}