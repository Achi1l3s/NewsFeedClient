package com.faist.vknewsclient.domain.usecases

import com.faist.vknewsclient.domain.entity.FeedPost
import com.faist.vknewsclient.domain.entity.PostComment
import com.faist.vknewsclient.domain.repository.NewsFeedRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(
    private val repository: NewsFeedRepository
) {

    operator fun invoke(feedPost: FeedPost): StateFlow<List<PostComment>> {
        return repository.getComments(feedPost)
    }
}