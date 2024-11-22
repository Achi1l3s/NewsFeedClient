package com.faist.vknewsclient.domain.usecases

import com.faist.vknewsclient.domain.repository.NewsFeedRepository
import javax.inject.Inject

class LoadNextDataUseCase @Inject constructor(
    private val repository: NewsFeedRepository
) {

    suspend operator fun invoke() {
        return repository.loadNextData()
    }
}