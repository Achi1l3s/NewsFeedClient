package com.faist.vknewsclient.presentation.comments

import com.faist.vknewsclient.domain.FeedPost
import com.faist.vknewsclient.domain.PostComment

sealed class CommentsScreenState {

    object Initial : CommentsScreenState()

    object Loading : CommentsScreenState()

    data class Comments(
        val feedPost: FeedPost,
        val comments: List<PostComment>,
        val nextCommentsIsLoading: Boolean = false
    ) : CommentsScreenState()
}