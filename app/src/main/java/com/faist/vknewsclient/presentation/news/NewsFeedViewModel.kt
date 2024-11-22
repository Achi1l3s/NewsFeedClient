package com.faist.vknewsclient.presentation.news

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faist.vknewsclient.domain.entity.FeedPost
import com.faist.vknewsclient.domain.usecases.ChangeLikeStatusUseCase
import com.faist.vknewsclient.domain.usecases.DeleteFeedPostUseCase
import com.faist.vknewsclient.domain.usecases.GetNewsFeedUseCase
import com.faist.vknewsclient.domain.usecases.LoadNextDataUseCase
import com.faist.vknewsclient.extensions.mergeWith
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class NewsFeedViewModel @Inject constructor(
    private val getNewsFeedUseCase: GetNewsFeedUseCase,
    private val loadNextDataUseCase: LoadNextDataUseCase,
    private val deleteFeedPostUseCase: DeleteFeedPostUseCase,
    private val changeLikeStatusUseCase: ChangeLikeStatusUseCase
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        Log.d("NewsFeedViewModel", "Exception caught by exception handler, removePost/changeLike")
    }
    private val newsFeedFlow = getNewsFeedUseCase()
    private val loadNextDataFlow = MutableSharedFlow<NewsFeedScreenState>()

    val screenState = newsFeedFlow
        .filter { it.isNotEmpty() }
        .map { NewsFeedScreenState.Posts(posts = it) as NewsFeedScreenState }
        .onStart { emit(NewsFeedScreenState.Loading) }
        .mergeWith(loadNextDataFlow)

    fun loadNextNewsFeed() {
        viewModelScope.launch {
            loadNextDataFlow.emit(
                NewsFeedScreenState.Posts(
                    posts = newsFeedFlow.value,
                    nextDataIsLoading = true
                )
            )
            loadNextDataUseCase()
        }
    }

    fun changeLikeStatus(feedPost: FeedPost) {
        viewModelScope.launch(exceptionHandler) {
            changeLikeStatusUseCase(feedPost)
        }
    }

    fun removeFeedPost(feedPost: FeedPost) {
        viewModelScope.launch(exceptionHandler) {
            deleteFeedPostUseCase(feedPost)
        }
    }
}