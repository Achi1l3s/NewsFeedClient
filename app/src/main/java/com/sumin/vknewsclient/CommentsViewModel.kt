package com.sumin.vknewsclient

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sumin.vknewsclient.utils.RandomFillBlankInfo
import com.sumin.vknewsclient.domain.FeedPost
import com.sumin.vknewsclient.domain.PostComment
import com.sumin.vknewsclient.ui.theme.CommentsScreenState
import kotlin.random.Random

class CommentsViewModel(
    feedPost: FeedPost
) : ViewModel() {

    private val fillBlankInfoCommentsAndTime = RandomFillBlankInfo()

    private val _screenState = MutableLiveData<CommentsScreenState>(CommentsScreenState.Initial)
    val screenState: LiveData<CommentsScreenState> = _screenState

    init {
        loadComments(feedPost)
    }

    private fun loadComments(feedPost: FeedPost) {
        val comments = mutableListOf<PostComment>().apply {
            repeat(10) {
                add(
                    PostComment(
                        id = it,
                        publicationDate = fillBlankInfoCommentsAndTime.getRandomTimeStamp(),
                        commentText = fillBlankInfoCommentsAndTime
                            .getFewSentences(
                                Random.nextInt(2, 10)
                            )
                    )
                )
            }
        }
        _screenState.value = CommentsScreenState
            .Comments(
                feedPost,
                comments = comments
            )
    }
}