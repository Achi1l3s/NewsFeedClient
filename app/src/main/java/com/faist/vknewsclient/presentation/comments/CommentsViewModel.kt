package com.faist.vknewsclient.presentation.comments

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.faist.vknewsclient.data.repository.NewsFeedRepository
import com.faist.vknewsclient.domain.FeedPost
import kotlinx.coroutines.launch

class CommentsViewModel(
    feedPost: FeedPost,
    application: Application
) : AndroidViewModel(application) {

    private val repository = NewsFeedRepository(application)

    private val _screenState = MutableLiveData<CommentsScreenState>(CommentsScreenState.Initial)
    val screenState: LiveData<CommentsScreenState> = _screenState

    init {
        _screenState.value = CommentsScreenState.Loading
        loadFeedPostComments(feedPost)
    }

    private fun loadFeedPostComments(feedPost: FeedPost) {
        viewModelScope.launch {
            Log.d("MediaCheck", "CommentsViewModel, 'loadFeedPostComments' BLOCK")
            try {
                repository.loadPostComments(feedPost)
                val comments = repository.postComments
                _screenState.value = CommentsScreenState
                    .Comments(
                        feedPost = feedPost,
                        comments = comments
                    )
            } catch (e: Exception) {
                Log.d("MediaCheck", "CommentsViewModel, 'loadFeedPostComments' is NULL, BLOCK")
            }
        }
    }

    fun loadNextComments() {
        viewModelScope.launch {
            val currentState = _screenState.value
            if (currentState is CommentsScreenState.Comments && !currentState.nextCommentsIsLoading) {

                _screenState.value = currentState.copy(nextCommentsIsLoading = true) // Устанавливаем индикатор загрузки следующей страницы
                Log.d("MediaCheck", "CommentsViewModel, 'loadNextComments == true' BLOCK")

                try {
                    _screenState.value = currentState.copy(
                        comments = repository.postComments,
                        nextCommentsIsLoading = false
                    )

                } catch (e: Exception) {
                    Log.e("Error", "Failed to load comments: ${e.message}")
                    _screenState.value = currentState.copy(nextCommentsIsLoading = false)
                }
            }
        }
    }
}


//viewModelScope.launch {
//    val oldComments = repository.postComments
//    val currentState = _screenState.value
//    if (currentState is CommentsScreenState.Comments && !currentState.nextCommentsIsLoading) {
//
//        _screenState.value = currentState.copy(nextCommentsIsLoading = true) // Устанавливаем индикатор загрузки следующей страницы
//        Log.d("MediaCheck", "CommentsViewModel, 'loadNextComments == true' BLOCK")
//
//        try {
//            val newComments = repository.loadPostComments(feedPost)
//            _screenState.value = currentState.copy(
//                comments = oldComments + newComments,
//                nextCommentsIsLoading = false
//            )
//
//        } catch (e: Exception) {
//            Log.e("Error", "Failed to load comments: ${e.message}")
//            _screenState.value = currentState.copy(nextCommentsIsLoading = false)
//        }
//    }
//}