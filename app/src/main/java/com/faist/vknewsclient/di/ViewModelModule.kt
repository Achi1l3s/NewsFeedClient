package com.faist.vknewsclient.di

import androidx.lifecycle.ViewModel
import com.faist.vknewsclient.presentation.comments.CommentsViewModel
import com.faist.vknewsclient.presentation.main.MainViewModel
import com.faist.vknewsclient.presentation.news.NewsFeedViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @ViewModelKey(NewsFeedViewModel::class)
    @Binds
    fun bindNewsFeedViewModel(impl: NewsFeedViewModel): ViewModel

    @IntoMap
    @ViewModelKey(MainViewModel::class)
    @Binds
    fun bindMainViewModel(impl: MainViewModel): ViewModel
}