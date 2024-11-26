package com.faist.vknewsclient.di

import android.content.Context
import com.faist.vknewsclient.domain.entity.FeedPost
import com.faist.vknewsclient.presentation.ViewModelFactory
import com.faist.vknewsclient.presentation.main.MainActivity
import dagger.BindsInstance
import dagger.Component


@ApplicationScope
@Component(
    modules = [
        DataModule::class,
        ViewModelModule::class
    ]
)
interface ApplicationComponent {

    fun getViewModelFactory(): ViewModelFactory

    fun getCommentsScreenComponentFactory(): CommentsScreenComponent.Factory

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance context: Context
        ): ApplicationComponent
    }
}