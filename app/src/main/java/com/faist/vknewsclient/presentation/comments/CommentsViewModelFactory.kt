package com.faist.vknewsclient.presentation.comments

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.faist.vknewsclient.domain.FeedPost

class CommentsViewModelFactory(
    private val feedPost: FeedPost,
    private val application: Application
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return CommentsViewModel(
            feedPost = feedPost,
            application = application
        ) as T
    }
}