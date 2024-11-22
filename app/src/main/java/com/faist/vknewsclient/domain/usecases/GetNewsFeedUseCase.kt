package com.faist.vknewsclient.domain.usecases

import com.faist.vknewsclient.domain.entity.FeedPost
import com.faist.vknewsclient.domain.repository.NewsFeedRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetNewsFeedUseCase @Inject constructor(
    private val repository: NewsFeedRepository
) {

    operator fun invoke(): StateFlow<List<FeedPost>> {
        return repository.getNewsFeed()
    }
}