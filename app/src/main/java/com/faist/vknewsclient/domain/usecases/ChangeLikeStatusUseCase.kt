package com.faist.vknewsclient.domain.usecases

import com.faist.vknewsclient.domain.entity.FeedPost
import com.faist.vknewsclient.domain.repository.NewsFeedRepository
import javax.inject.Inject

class ChangeLikeStatusUseCase @Inject constructor(
    private val repository: NewsFeedRepository
) {

    suspend operator fun invoke(feedPost: FeedPost) {
        return repository.changeLikeStatus(feedPost)
    }
}